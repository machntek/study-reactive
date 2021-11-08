package com.machntek.reactive.live5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MachntekAsyncApplication {
    @RestController
    public static class MyController {
        @GetMapping("/rest")
        public String rest() {
            return "rest";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MachntekAsyncApplication.class);
    }
}
