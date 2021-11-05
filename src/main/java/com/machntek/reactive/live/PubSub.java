package com.machntek.reactive.live;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.TimeUnit;

public class PubSub {
    public static void main(String[] args) throws InterruptedException {
        // Publisher <- Observable
        // Subscriber <- Observer

        //         필수 호출    n번호출        둘중 하나 호출
        // 프로토콜 onSubscribe onNext* (onError | onComplete)?

        List<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);
        ExecutorService es = Executors.newCachedThreadPool();

        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {
                Iterator<Integer> it = itr.iterator();

                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {

                        es.execute(() -> {
                            int i = 0;
                            try {
                                // 자유변수는 람다 내부에서 고칠 수 없다.
                                while (i++ < n) {
                                    if (it.hasNext()) {
                                        subscriber.onNext(it.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            }
                            catch (RuntimeException e) {
                                subscriber.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription;

            // subscribe 하는 즉시 호출해줘야 한다.
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println(Thread.currentThread().getName() + " onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + " onNext " + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);

        es.awaitTermination(10, TimeUnit.HOURS);
        es.shutdown();

    }
}
