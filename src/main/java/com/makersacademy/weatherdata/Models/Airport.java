package com.makersacademy.weatherdata.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "airports")
@Getter
@Setter
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "icao_code", length = 4)
    private String icaoCode;

    @Column(name = "iata_code", length = 3)
    private String iataCode;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "lat_decimal")
    private Double latDecimal;

    @Column(name = "lon_decimal")
    private Double lonDecimal;

    @Column(name = "altitude")
    private Integer altitude;
}
