package com.machntek.reactive.live4;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class FutureEx {
    interface SuccessCallback {
        void onSuccess(String result);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    // 비동기작업 처리 방법 - Future or callback
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        },
                System.out::println);

        es.execute(f);
        es.shutdown();
    }
}
