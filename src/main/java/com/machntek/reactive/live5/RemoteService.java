package com.machntek.reactive.live5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RemoteService {

    @RestController
    public static class MyController {
        @GetMapping("/service")
        public String service(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/service";
        }
    }

    public static void main(String[] args) {
        System.setProperty("SERVER.PORT", "8081");  // 내장 톰캣이 이 포트값을 오버라이딩해서 씀
        System.setProperty("server.tomcat.max-threads", "1000");
        SpringApplication.run(RemoteService.class);
    }
}
