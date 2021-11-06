package com.machntek.reactive.live3;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// subscribeOn : publisher가 아주 느린경우(blocking IO를 사용한다거나), subscriber는 빠르게 처리할 때 -> subscribeOn 사용(스케쥴러)
// 즉, 데이터 생성하는데 오래걸리거나 에측할 수 없는 경우!
//
// publishOn : publihser가 작업 수행/전달/저장 등을 수행하는 작업에 비해, subscriber의 작업이 상대적으로 느린 경우 사용
//
// rxJava, reactor, reactive-stream 등 표준적으로 하나의 publisher가 데이터 생성해서 던져주는거는 멀티스레드에서 publisher가 onNext를 호출하면 안되게 돼있음(반드시 단일쓰레드 안에서 넘어가야함)
@Slf4j
public class SchedulerEx {
    public static void main(String[] args) {
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {

                // 데이터 생성 부분
                @Override
                public void request(long n) {
                    log.debug("request");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5);
                    sub.onComplete();
                }

                @Override
                public void cancel() {

                }
            });

        };

//        Publisher<Integer> subOnPub = sub -> {
//            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
//                @Override
//                public String getThreadNamePrefix() { return "subOn-"; }
//            });
//            es.execute(() -> pub.subscribe(sub));
//        };

        Publisher<Integer> pubOnPub = sub -> {
            pub.subscribe(new Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                    @Override
                    public String getThreadNamePrefix() { return "pubOn-"; }
                });
                @Override
                public void onSubscribe(Subscription s) {
                    sub.onSubscribe(s);
                }

                @Override
                public void onNext(Integer integer) {
                    es.execute(() -> sub.onNext(integer));
                }

                @Override
                public void onError(Throwable t) {
                    es.execute(() -> sub.onError(t));
                    // shutdown은 현재 진행중인 쓰레드가 작업을 다 마치면 종료. shutdownNow는 강제로 interrupt를 발생시켜서 종료시킴.
                    es.shutdown();
                }

                @Override
                public void onComplete() {
                    es.execute(() -> sub.onComplete());
                    es.shutdown();
                }
            });
        };

        pubOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                log.debug("onNext : {}", i);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError: {}", t);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        });

        log.debug("exit");

    }
}
