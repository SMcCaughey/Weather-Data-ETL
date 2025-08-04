package com.makersacademy.weatherdata.Controllers;

import com.makersacademy.weatherdata.Models.WeatherData;
import com.makersacademy.weatherdata.Service.WeatherAPI;
import com.makersacademy.weatherdata.Service.CityService;
import com.makersacademy.weatherdata.Models.City;
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
import java.util.stream.Collectors;

@Controller
public class WeatherController {

    @Autowired
    private WeatherAPI weatherAPI;

    @Autowired
    private CityService cityService;

    @GetMapping("/average-weather/{cityName}")
    public String showWeather(@PathVariable("cityName") String cityName, Model model) {
        cityName = cityName.trim();
        String displayCityName = java.util.Arrays.stream(cityName.split(" "))
            .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
            .reduce((a, b) -> a + " " + b).orElse(cityName);
        Optional<City> cityOpt = cityService.findCityByName(cityName);
        if (cityOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Sorry, we don't have any data for '" + displayCityName + "'. Please try another city.");
            return "error";
        }
        City city = cityOpt.get();
        WeatherData weatherData = weatherAPI.fetchWeatherData(Double.parseDouble(String.valueOf(city.getLat())), Double.parseDouble(String.valueOf(city.getLng())));
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

        model.addAttribute("weatherData", weatherData);
        model.addAttribute("averageTemperature", String.format("%.1f", average));
        model.addAttribute("formattedTimes", formattedTimes);
        model.addAttribute("formattedTemps", formattedTemps);
        model.addAttribute("cityName", displayCityName);
        model.addAttribute("cities", cityService.getAllCities());

        return "average-weather";
    }

    @PostMapping("/average-weather/{cityName}")
    public String postWeatherPath(@PathVariable("cityName") String cityName, @RequestParam(value = "cityName", required = false) String cityNameParam, Model model) {
        String effectiveCityName = cityNameParam != null && !cityNameParam.isBlank() ? cityNameParam.trim() : cityName.trim();
        // Capitalise first letter of each word for display
        String displayCityName = java.util.Arrays.stream(effectiveCityName.split(" "))
            .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
            .reduce((a, b) -> a + " " + b).orElse(effectiveCityName);
        return showWeather(displayCityName, model);
    }

    @GetMapping("/")
    public String home(Model model) {
        // Default to London for the homepage
        return showWeather("London", model);
    }
}