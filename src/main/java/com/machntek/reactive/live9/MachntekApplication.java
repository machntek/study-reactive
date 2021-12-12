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
        log.info("pos1");

        // Mono를 만든다는건 Publisher만 만들어서 리턴으로 넘긴다는것. 그러면 스프링 알아서 subscribe를 걸어줌
        Mono m = Mono.just("Hello Webflux").doOnNext(c -> log.info(c)).log();// static 팩토리 메소드
        log.info("pos2");
        return m;
    }

    public static void main(String[] args) {
        SpringApplication.run(MachntekApplication.class, args);
    }
}
