package com.makersacademy.weatherdata.Repositories;

import com.makersacademy.weatherdata.Models.AirportWeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportWeatherForecastRepository extends JpaRepository<AirportWeatherForecast, Long> {

    @Query("SELECT awf FROM AirportWeatherForecast awf WHERE UPPER(awf.airportName) = UPPER(:airportName)")
    List<AirportWeatherForecast> findByAirportNameIgnoreCase(@Param("airportName") String airportName);

    @Query("SELECT awf FROM AirportWeatherForecast awf WHERE UPPER(awf.cityName) = UPPER(:cityName)")
    List<AirportWeatherForecast> findByCityNameIgnoreCase(@Param("cityName") String cityName);
}
