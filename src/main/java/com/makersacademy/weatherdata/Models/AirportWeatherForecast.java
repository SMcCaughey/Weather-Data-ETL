package com.makersacademy.weatherdata.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "airport_weather_forecast")
@Getter
@Setter
public class AirportWeatherForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_name", nullable = false)
    private String cityName;

    @Column(name = "airport_name", nullable = false)
    private String airportName;

    @Column(name = "start_date", nullable = false)
    private String startDate; // format: dd/mm/yyyy

    @Column(name = "end_date", nullable = false)
    private String endDate; // format: dd/mm/yyyy

    @Column(name = "average_temperature", nullable = false)
    private Double averageTemperature;
}
