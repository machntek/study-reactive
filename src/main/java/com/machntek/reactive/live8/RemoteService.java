package com.machntek.reactive.live8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class RemoteService {

    @RestController
    public static class MyController {
        @Bean
        TomcatReactiveWebServerFactory tomcatReactiveWebServerFactory() {
            return new TomcatReactiveWebServerFactory();
        }
        @GetMapping("/service")
        public Mono<String> service(String req) throws InterruptedException {
            Thread.sleep(1000);
//            throw new RuntimeException();
            return Mono.just(req + "/service");
        }

        @GetMapping("/service2")
        public Mono<String> service2(String req) throws InterruptedException {
            Thread.sleep(1000);
            return Mono.just(req + "/service2");
        }
    }

    public static void main(String[] args) {
        System.setProperty("SERVER.PORT", "8081");  // 내장 톰캣이 이 포트값을 오버라이딩해서 씀
        System.setProperty("server.tomcat.threads.max", "1000");
        SpringApplication.run(RemoteService.class);
    }
}
