package com.machntek.reactive.live7;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@EnableAsync
@SpringBootApplication
public class MachntekAsyncApplication {


    @RestController
    public static class MyController {
        public static final String URL1 = "http://localhost:8081/service?req={req}";
        public static final String URL2 = "http://localhost:8081/service2?req={req}";
        @Autowired
        MyService myService;
        // Netty가 기본적으로 call할때 쓰레드 만드는 갯수 : 프로세스 갯수 * 2개
        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

        @GetMapping("/rest")
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            toCF(rt.getForEntity(URL1, String.class, "hello" + idx))
                .thenCompose(s -> {
                    if (1==1) throw new RuntimeException("ERROR");
                    return toCF(rt.getForEntity(URL2, String.class, s.getBody()));
                }) // Netty를 실행하는 쪽에서의 쓰레드를 타고 옴.
                .thenApplyAsync(s2 -> myService.work((s2.getBody()))) // (thenApply 사용시) 이 작업이 수행되는 동안에 얘를 call 한 thread(Netty 실행 쓰레드)를 계속 물고있음. 그래서 오랫동안 작업을 수행해야 하는 경우 thenApplyAsync로 별개의 쓰레드 사용.
                .thenAccept(s3 -> dr.setResult(s3)) // 오브젝트에 메소드 하나 호출하는 간단한 작업을 수행하고 끝남. 작업 수행 후 Netty 쓰레드는 바로 리턴되므로 영향 없음. DeferredResult로 http응답이 나가는 작업은 서블릿 쓰레드풀을 이용하여 쓰레드를 할당받아 처리됨.
                .exceptionally(e -> { dr.setErrorResult(e.getMessage()); return (Void)null; });

            return dr;
        }

        <T> CompletableFuture<T> toCF(ListenableFuture<T> lf) {
            CompletableFuture<T> cf = new CompletableFuture<T>(); // 완료가 되지 않은 비동기 작업의 결과를 담고있는 오브젝트. CompletableFuture 자체가 비동기 작업은 아님. 그 작업의 결과를 나타냄.
            lf.addCallback(s -> cf.complete(s), e -> cf.completeExceptionally(e));
            return cf;
        }
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPool() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(1);
        te.setMaxPoolSize(1);
        te.initialize();
        return te;
    }

    @Service
    public static class MyService {
        public String work(String req) {
            return req + "/asyncwork";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MachntekAsyncApplication.class);
    }
}
