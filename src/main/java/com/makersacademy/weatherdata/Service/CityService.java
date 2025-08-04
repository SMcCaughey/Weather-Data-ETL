package com.makersacademy.weatherdata.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.makersacademy.weatherdata.Models.City;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    private List<City> cities;

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("Cities.json");
            cities = mapper.readValue(is, new TypeReference<List<City>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load cities", e);
        }
    }

    public Optional<City> findCityByName(String name) {
        return cities.stream()
                .filter(c -> c.getCity().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<City> getAllCities() {
        return cities;
    }
}

