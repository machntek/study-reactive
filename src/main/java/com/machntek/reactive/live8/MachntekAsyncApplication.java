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
            // 비동기 Non-Blocking 이라는 관점에서 보면 AsyncRestTemplate + DeferredResult 와 똑같이 동작함
            return client.get().uri(URL1, idx).exchange()   // Mono<ClientResponse>
                    .flatMap(c -> c.bodyToMono(String.class))   // Mono<String>
                    .flatMap(res1 -> client.get().uri(URL2, res1).exchange())  // Mono<ClientResponse>
                    .flatMap(c -> c.bodyToMono(String.class));  // Mono<String>
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
