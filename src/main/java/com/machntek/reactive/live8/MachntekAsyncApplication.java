package com.machntek.reactive.live8;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@EnableAsync
@SpringBootApplication
public class MachntekAsyncApplication {


    @RestController
    public static class MyController {
        public static final String URL1 = "http://localhost:8081/service?req={req}";
        public static final String URL2 = "http://localhost:8081/service2?req={req}";

        // AsyncRestTemplate 과 비슷
        WebClient client = WebClient.create();

        @Autowired
        MyService myService;

        @GetMapping("/rest")
        public Mono<String> rest(int idx) {
            // ClientResponse 는 ResponseEntity 와 비슷
            /**
             * ClientResponse 는 ResponseEntity 와 비슷함.
             * 이렇게 정의하는것 만으로는 api 호출이 되지 않음.
             *
             * Mono는 Publisher 인터페이스를 구현함.
             * Publisher는 만들어 놓는다고 해서 자기가 알아서 publishing을 하는게 아님. 비동기 작업을 수행해서 그 결과를 퍼블리싱하는 코드를 정의한 것.
             * Subscriber가 subscribe하지 않으면 데이터를 쏘지 않음
             */
            Mono<ClientResponse> res = client.get().uri(URL1, idx).exchange();  // 이렇게 정의하는것 만으로는 api 호출이 되지 않음.

            return res.flatMap(clientResponse -> clientResponse.bodyToMono(String.class));
        }
    }

    @Service
    public static class MyService {
        public String work(String req) {
            return req + "/asyncwork";
        }
    }

    public static void main(String[] args) {
        System.setProperty("reactor.ipc.netty.workerCount", "2");
        System.setProperty("reactor.ipc.netty.pool.maxConnections", "2000");
        SpringApplication.run(MachntekAsyncApplication.class);
    }
}
