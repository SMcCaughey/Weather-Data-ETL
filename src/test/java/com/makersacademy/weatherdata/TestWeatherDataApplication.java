package com.makersacademy.weatherdata;

import org.springframework.boot.SpringApplication;

public class TestWeatherDataApplication {

    public static void main(String[] args) {
        SpringApplication.from(WeatherDataApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
