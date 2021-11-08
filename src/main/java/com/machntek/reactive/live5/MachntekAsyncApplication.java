package com.machntek.reactive.live5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MachntekAsyncApplication {


    @RestController
    public static class MyController {
        AsyncRestTemplate rt = new AsyncRestTemplate();

        @GetMapping("/rest")
        public ListenableFuture<ResponseEntity<String>> rest(String idx) {
            return rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MachntekAsyncApplication.class);
    }
}
