package com.machntek.reactive.live4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Future;

// 장시간 수행되는 작업에 대한 결과를 리턴하는 방법
// 1. 결과를 DB에 넣고, 클라이언트가 궁금할때 DB 액세스
// 2. Future라는 핸들러를 세션에 저장하고 바로 리턴. 궁금하면 다음 컨트롤러 메소드에서, 세션에서 Future값 꺼내서, isDone() 으로 결과 나왔는지 확인
// 위 2개는 아주 예전(오래된) 해결책.

@EnableAsync
@SpringBootApplication
@Slf4j
public class MachntekAsyncApplication {

    @Component
    public static class MyService {
        @Async
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(2000);
            return new AsyncResult<>("Hello");
        }
    }

    public static void main(String[] args) {
        try (ConfigurableApplicationContext c = SpringApplication.run(MachntekAsyncApplication.class, args)) {

        }
    }

    @Autowired MyService myService;

    @Bean
    ApplicationRunner run() {
        return args -> {
            log.info("run()");
            ListenableFuture<String> f = myService.hello();
            f.addCallback(s -> System.out.println(s), e -> System.out.println(e.getMessage()));

            log.info("exit");
        };
    }
}
