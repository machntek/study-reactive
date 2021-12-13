package com.machntek.reactive.live10;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Slf4j
@RestController
public class MachntekApplication {

    @GetMapping("/event/{id}")
    Mono<List<Event>> event(@PathVariable long id) {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Mono.just(list); // List타입 자체가 하나의 element임.
    }

    @GetMapping(value="/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Flux.fromIterable(list); // 각 element에 map 등의 reactor 라이브러리의 오퍼레이터 작업 걸 수 있다.
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
