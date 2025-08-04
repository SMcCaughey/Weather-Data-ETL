package com.makersacademy.weatherdata.Repositories;

import com.makersacademy.weatherdata.Models.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    @Query("SELECT a FROM Airport a WHERE UPPER(a.city) = UPPER(:cityName)")
    List<Airport> findByCityIgnoreCase(@Param("cityName") String cityName);

    @Query("SELECT a FROM Airport a WHERE UPPER(a.name) = UPPER(:airportName)")
    Optional<Airport> findByNameIgnoreCase(@Param("airportName") String airportName);

    @Query("SELECT DISTINCT a.city FROM Airport a ORDER BY a.city")
    List<String> findAllCities();
}
