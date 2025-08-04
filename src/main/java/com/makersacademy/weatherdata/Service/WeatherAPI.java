package com.makersacademy.weatherdata.Service;

import com.makersacademy.weatherdata.Models.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherAPI {

    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherData fetchWeatherData(double latitude, double longitude) {
        String apiUrl = String.format("https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m&timezone=GMT", latitude, longitude);
        return restTemplate.getForObject(apiUrl, WeatherData.class);
    }
}