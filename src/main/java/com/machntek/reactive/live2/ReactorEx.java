package com.machntek.reactive.live2;

import reactor.core.publisher.Flux;

public class ReactorEx {
    public static void main(String[] args) {
        // Flux는 일종의 Publisher
        Flux.<Integer>create(e -> {
            e.next(1);
            e.next(2);
            e.next(3);
            e.complete();
        })
        .log()
        .map(s -> s*10)
        .reduce(0, (a,b) -> a+b)
        .log()
        .subscribe(System.out::println);
    }
}
