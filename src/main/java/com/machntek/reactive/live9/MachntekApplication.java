package com.machntek.reactive.live9;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@Slf4j
public class MachntekApplication {
    @GetMapping("/")
    Mono<String> hello() {
        return Mono.just("Hello Webflux").log();  // static 팩토리 메소드
    }

    public static void main(String[] args) {
        SpringApplication.run(MachntekApplication.class, args);
    }
}
