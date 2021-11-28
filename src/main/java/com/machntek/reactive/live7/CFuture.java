package com.machntek.reactive.live7;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//                                                        // Future 오브젝트를 만듦(완료된 상태)
//        CompletableFuture<Integer> f = CompletableFuture.completedFuture(1);

//        CompletableFuture<Integer> f = new CompletableFuture<>(); // 아직 작업이 완료되지 않은 Future
//        f.complete(2);  // 작업이 완료된 Future
//        System.out.println(f.get());

//        CompletableFuture<Integer> f = new CompletableFuture<>();
//        f.completeExceptionally(new RuntimeException()); // 완료가 되긴 했는데 예외가 발생했기 때문에 완료가 된 상황이다 라는 의미를 담음. 예외가 발생했다라는 사실을 담고있을 뿐임. 누군가에게 노출하는건 아님.
//        System.out.println(f.get()); // 이때 예외가 던져짐

        // 쓰레드풀 예시
        ExecutorService es = Executors.newFixedThreadPool(10);

        // 자바7부터는 풀을 아무것도 설정하지 않으면 ForkJoinPool의 commonPool이라는거의 worker가 동작함.
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("runAsync");
                    return 1;
                }, es)
                .thenCompose(s -> { // Function의 리턴타입이 CompletableFuture인 경우, thenCompose 사용하면 됨.(thenApply가 Stream.map이면 thenCompose는 Stream.flatMap과 비슷)
                    log.info("thenApply {}", s);
                    return CompletableFuture.completedFuture(s + 1); // CompletableFuture를 리턴하는 케이스는 추후 설명.
                })
                .thenApplyAsync(s2 -> {
                    log.info("thenApply {}", s2);
                    return s2 * 3;
                }, es)
                .exceptionally(e -> -10) // 값을 복구
                .thenAcceptAsync(s3 -> log.info("thenAccept {}", s3), es);
        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
