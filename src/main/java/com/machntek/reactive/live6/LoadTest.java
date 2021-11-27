package com.machntek.reactive.live6;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {
    static AtomicInteger counter = new AtomicInteger(0);
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100); // 코어쓰레드 100개

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest?idx={idx}";

        CyclicBarrier barrier = new CyclicBarrier(101);



        for (int i=0; i<100; i++) {
            es.submit(() -> {
                int idx = counter.addAndGet(1);
                log.info("Thread {}", idx);

                barrier.await(); // 쓰레드 동기화. await을 만난 쓰레드 숫자가 위의 101에 도달할때까지 블록킹. 101번째 쓰레드가 실행되는 순간 모든 쓰레드의 블로킹이 풀려서 한꺼번에 수행됨.

                StopWatch sw = new StopWatch();
                sw.start();

                String res = rt.getForObject(url, String.class, idx);

                sw.stop();
                log.debug("Elapsed: {} {} / {}", idx, sw.getTotalTimeSeconds(), res);
                return null;
            });
        }

        barrier.await();
        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());
    }
}
