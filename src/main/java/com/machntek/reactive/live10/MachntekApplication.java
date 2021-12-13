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

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
        Flux<Event> es = Flux.<Event, Long>generate(()->1L, (id, sink)-> {
                    sink.next(new Event(id, "value" + id));
                    return id+1;
                });
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));  // interval은 일정한 주기로 0부터 시작하는 값을 계속 던져줌.

        return Flux.zip(es, interval).map(tu->tu.getT1()).take(10); // 모든 Flux 는 zip으로 다 묶을수 있다(zipping)
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
