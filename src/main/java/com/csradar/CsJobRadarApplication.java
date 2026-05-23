package com.csradar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CsJobRadarApplication {
    public static void main(String[] args) {
        SpringApplication.run(CsJobRadarApplication.class, args);
    }
}
