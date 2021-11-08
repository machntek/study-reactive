package com.machntek.reactive.live5;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MachntekAsyncApplication {


    @RestController
    public static class MyController {
        // Netty가 기본적으로 call할때 쓰레드 만드는 갯수 : 프로세스 갯수 * 2개
        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

        @GetMapping("/rest")
        public ListenableFuture<ResponseEntity<String>> rest(int idx) {
            return rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MachntekAsyncApplication.class);
    }
}
