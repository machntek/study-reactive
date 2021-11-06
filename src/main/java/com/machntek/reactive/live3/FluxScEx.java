package com.machntek.reactive.live3;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

// user thread(유저가 만든 쓰레드)는 메인쓰레드가 종료돼도 종료되지 않음
@Slf4j
public class FluxScEx {
    public static void main(String[] args) throws InterruptedException {
//        Flux.range(1, 10)
//                .publishOn(Schedulers.newSingle("pub"))
//                .log()
//                .subscribeOn(Schedulers.newSingle("sub"))
//                .subscribe(System.out::println); // subscribe에 파라미터로 하나만 넣으면 onNext를 처리하는 람다식을 받는거임
//
//        System.out.println("exit");

        // 쓰레드 종류 : user thread, daemon thread
        // jvm은 user thread가 하나도 남아있지 않고 daemon thread들만 남아있으면 강제로 종료
        // user thread는 하나라도 남아있으면 종료되지 않음
        //
        // Flux.interval이 데몬 쓰레드를 쓴 이유 - 서버에 유저쓰레드가 정상적으로 종료되지 않은채로 남아서 서버가 안전하게 shutdown되는 등의 작업에 문제가 안생기게 하기 위해서(추측)
        // 서버에 수많은 main쓰레드가 돌아가고 있을텐데, 그 위에서 얘를 데몬쓰레드로 만드는게 편할거라는 판단.(추측)

        Flux.interval(Duration.ofMillis(200))
                .subscribe(s -> log.debug("onNext:{}", s));

        log.debug("exit");
        TimeUnit.SECONDS.sleep(5);
    }
}
