package com.meetmate.group.entity;

import com.meetmate.group.enums.BudgetLevel;
import com.meetmate.group.enums.FoodPreference;
import com.meetmate.group.enums.TransportMode;
import com.meetmate.group.enums.TravelWillingness;
import com.meetmate.group.util.TransportModesConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "member_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MemberPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false)
    private UUID userId;

    private String currentLocation;

    @Column(precision = 10, scale = 8)
    private BigDecimal currentLat;

    @Column(precision = 11, scale = 8)
    private BigDecimal currentLng;

    @Column(name = "transport_modes", columnDefinition = "text[]")
    @Convert(converter = TransportModesConverter.class)
    private Set<TransportMode> transportModes;

    @Column(name = "max_distance_km")
    private Integer maxDistanceKm;

    private TravelWillingness travelWillingness;

    private BudgetLevel budgetLevel;

    private FoodPreference foodPreference;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

