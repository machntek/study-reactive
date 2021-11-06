package com.machntek.reactive.live3;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxScEx {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println); // subscribe에 파라미터로 하나만 넣으면 onNext를 처리하는 람다식을 받는거임

        System.out.println("exit");
    }
}
