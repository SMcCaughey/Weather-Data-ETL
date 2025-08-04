package com.makersacademy.weatherdata.Controllers;

import com.makersacademy.weatherdata.Models.WeatherData;
import com.makersacademy.weatherdata.Models.Airport;
import com.makersacademy.weatherdata.Models.AirportWeatherForecast;
import com.makersacademy.weatherdata.Service.WeatherAPI;
import com.makersacademy.weatherdata.Service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class WeatherController {

    @Autowired
    private WeatherAPI weatherAPI;

    @Autowired
    private AirportService airportService;

    @GetMapping("/average-weather/{cityName}")
    public String showWeather(@PathVariable("cityName") String cityName,
                            @RequestParam(value = "airportName", required = false) String airportName,
                            Model model) {
        cityName = cityName.trim();
        String displayCityName = capitalizeWords(cityName);

        // Find airports for the city
        List<Airport> airports = airportService.findAirportsByCity(cityName);
        if (airports.isEmpty()) {
            model.addAttribute("errorMessage", "Sorry, we don't have any airports for '" + displayCityName + "'. Please try another city.");
            return "error";
        }

        // If multiple airports, show selection page unless specific airport requested
        if (airports.size() > 1 && airportName == null) {
            model.addAttribute("cityName", displayCityName);
            model.addAttribute("airports", airports);
            model.addAttribute("cities", airportService.getAllCities());
            return "airport-selection";
        }

        // Select specific airport or default to first one
        Airport selectedAirport;
        if (airportName != null) {
            selectedAirport = airports.stream()
                .filter(airport -> airport.getName().equalsIgnoreCase(airportName))
                .findFirst()
                .orElse(airports.get(0));
        } else {
            selectedAirport = airports.get(0);
        }

        // Get weather data for the selected airport coordinates
        WeatherData weatherData = weatherAPI.fetchWeatherData(selectedAirport.getLatDecimal(), selectedAirport.getLonDecimal());
        List<Double> temps = weatherData.getHourly().getTemperature_2m();
        List<String> times = weatherData.getHourly().getTime();

        // Filter for current week
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(java.time.DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

        List<Double> weekTemps = new java.util.ArrayList<>();
        List<String> formattedTimes = new java.util.ArrayList<>();
        List<String> formattedTemps = new java.util.ArrayList<>();

        for (int i = 0; i < times.size(); i++) {
            LocalDateTime time = LocalDateTime.parse(times.get(i), inputFormatter);
            if (!time.isBefore(startOfWeek) && time.isBefore(endOfWeek)) {
                weekTemps.add(temps.get(i));
                formattedTimes.add(time.format(outputFormatter));
                formattedTemps.add(String.format("%.1f", temps.get(i)));
            }
        }

        double average = weekTemps.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // Save forecast data to database
        airportService.saveWeatherForecast(displayCityName, selectedAirport.getName(),
                                         startOfWeek, endOfWeek.minusDays(1), average);

        model.addAttribute("weatherData", weatherData);
        model.addAttribute("averageTemperature", String.format("%.1f", average));
        model.addAttribute("formattedTimes", formattedTimes);
        model.addAttribute("formattedTemps", formattedTemps);
        model.addAttribute("cityName", displayCityName);
        model.addAttribute("airportName", selectedAirport.getName());
        model.addAttribute("airportCode", selectedAirport.getIataCode());
        model.addAttribute("airports", airports);
        model.addAttribute("cities", airportService.getAllCities());

        return "average-weather";
    }

    @PostMapping("/average-weather/{cityName}")
    public String postWeatherPath(@PathVariable("cityName") String cityName,
                                @RequestParam(value = "cityName", required = false) String cityNameParam,
                                @RequestParam(value = "airportName", required = false) String airportNameParam,
                                Model model) {
        String effectiveCityName = cityNameParam != null && !cityNameParam.isBlank() ?
                                 cityNameParam.trim() : cityName.trim();
        String displayCityName = capitalizeWords(effectiveCityName);
        return showWeather(displayCityName, airportNameParam, model);
    }

    @GetMapping("/")
    public String home(Model model) {
        // Default to London Heathrow for the homepage
        return showWeather("London", "Heathrow", model);
    }

    // New endpoint to query by airport name
    @GetMapping("/airport-weather/{airportName}")
    public String showWeatherByAirport(@PathVariable("airportName") String airportName, Model model) {
        List<AirportWeatherForecast> forecasts = airportService.getForecastsByAirportName(airportName);

        if (forecasts.isEmpty()) {
            model.addAttribute("errorMessage", "No weather data found for airport: " + airportName);
            return "error";
        }

        model.addAttribute("forecasts", forecasts);
        model.addAttribute("airportName", capitalizeWords(airportName));

        return "airport-forecast";
    }

    // New endpoint to handle airport forecast search via GET parameter
    @GetMapping("/airport-weather")
    public String searchAirportForecast(@RequestParam("airportName") String airportName, Model model) {
        return showWeatherByAirport(airportName, model);
    }

    private String capitalizeWords(String text) {
        return java.util.Arrays.stream(text.split(" "))
            .map(word -> word.isEmpty() ? word :
                 Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
            .reduce((a, b) -> a + " " + b).orElse(text);
    }
}