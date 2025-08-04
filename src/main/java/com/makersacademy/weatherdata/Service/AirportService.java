package com.makersacademy.weatherdata.Service;

import com.makersacademy.weatherdata.Models.Airport;
import com.makersacademy.weatherdata.Models.AirportWeatherForecast;
import com.makersacademy.weatherdata.Repositories.AirportRepository;
import com.makersacademy.weatherdata.Repositories.AirportWeatherForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AirportService {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirportWeatherForecastRepository forecastRepository;

    public List<Airport> findAirportsByCity(String cityName) {
        return airportRepository.findByCityIgnoreCase(cityName);
    }

    public Optional<Airport> findAirportByName(String airportName) {
        return airportRepository.findByNameIgnoreCase(airportName);
    }

    public List<String> getAllCities() {
        return airportRepository.findAllCities();
    }

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public void saveWeatherForecast(String cityName, String airportName,
                                   LocalDateTime startDate, LocalDateTime endDate,
                                   double averageTemperature) {
        AirportWeatherForecast forecast = new AirportWeatherForecast();
        forecast.setCityName(cityName);
        forecast.setAirportName(airportName);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        forecast.setStartDate(startDate.format(dateFormatter));
        forecast.setEndDate(endDate.format(dateFormatter));
        forecast.setAverageTemperature(averageTemperature);

        forecastRepository.save(forecast);
    }

    public List<AirportWeatherForecast> getForecastsByAirportName(String airportName) {
        return forecastRepository.findByAirportNameIgnoreCase(airportName);
    }

    public List<AirportWeatherForecast> getForecastsByCityName(String cityName) {
        return forecastRepository.findByCityNameIgnoreCase(cityName);
    }
}
