package com.meetmate.place.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePlaceRequest {

    @Size(max = 255)
    private String name;

    @Size(max = 50)
    private String category;

    @Size(max = 100)
    private String area;

    private String address;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal lat;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal lng;

    @Size(max = 20)
    private String costLevel;

    private Boolean hasVeg;
    private Boolean hasNonVeg;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal rating;

    private Boolean active;
}

