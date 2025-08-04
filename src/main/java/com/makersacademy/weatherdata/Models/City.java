package com.makersacademy.weatherdata.Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class City {
    private String city;
    private double lat;
    private double lng;
    private String country;
    private String iso2;
    private String admin_name;
    private String capital;
    private long population;
    private long population_proper;
}

