package com.meetmate.places;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PlaceRecommendationApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlaceRecommendationApplication.class, args);
    }
}
