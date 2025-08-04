package com.makersacademy.weatherdata.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class WeatherData {
    private Hourly hourly;

    @Setter
    @Getter
    public static class Hourly {
        private List<String> time;
        private List<Double> temperature_2m;

    }
}
