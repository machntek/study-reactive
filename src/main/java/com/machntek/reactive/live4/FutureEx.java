package com.machntek.reactive.live4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class FutureEx {

    // 비동기작업 처리 방법 - Future or callback
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        FutureTask<String> f = new FutureTask<>(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        });

        es.execute(f);

        System.out.println(f.isDone());
        Thread.sleep(2100);
        log.info("Exit");
        System.out.println(f.isDone());
        System.out.println(f.get());
    }
}
