package com.machntek.reactive.live10;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
@RestController
public class MachntekApplication {

    @GetMapping("/event/{id}")
    Mono<Event> event(@PathVariable long id) {
        return Mono.just(new Event(id, "event" + id));
    }

    @GetMapping("/events")
    Flux<Event> events() {
        return Flux.just(new Event(1L, "event1"), new Event(2L, "event2"));
    }

    public static void main(String[] args) {
        SpringApplication.run(MachntekApplication.class, args);
    }

    @Data @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }
}
