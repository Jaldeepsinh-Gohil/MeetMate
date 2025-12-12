package com.meetmate.group.util;

import com.meetmate.group.enums.TransportMode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class TransportModesConverter implements AttributeConverter<Set<TransportMode>, String> {

    @Override
    public String convertToDatabaseColumn(Set<TransportMode> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        // Postgres array literal e.g. {BIKE,CAR}
        String joined = attribute.stream()
            .map(Enum::name)
            .collect(Collectors.joining(","));
        return "{" + joined + "}";
    }

    @Override
    public Set<TransportMode> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.length() < 2) {
            return Collections.emptySet();
        }
        String trimmed = dbData.substring(1, dbData.length() - 1); // remove {}
        if (trimmed.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(trimmed.split(","))
            .map(String::trim)
            .map(TransportMode::valueOf)
            .collect(Collectors.toSet());
    }
}

