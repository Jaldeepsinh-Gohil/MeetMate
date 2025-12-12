package com.meetmate.place.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 50)
    private String category; // RESTAURANT, CAFE, FOOD_COURT, MALL, ENTERTAINMENT

    @Size(max = 100)
    private String area;

    @Column(columnDefinition = "text")
    private String address;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Column(precision = 10, scale = 8)
    private BigDecimal lat;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Column(precision = 11, scale = 8)
    private BigDecimal lng;

    @NotBlank
    @Size(max = 20)
    private String costLevel; // LOW, MEDIUM, HIGH

    private boolean hasVeg;
    private boolean hasNonVeg;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    private boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;
}

