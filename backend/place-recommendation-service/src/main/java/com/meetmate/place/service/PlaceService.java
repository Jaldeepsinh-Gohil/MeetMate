package com.meetmate.place.service;

import com.meetmate.place.dto.request.CreatePlaceRequest;
import com.meetmate.place.dto.request.UpdatePlaceRequest;
import com.meetmate.place.dto.response.PlaceResponse;
import com.meetmate.place.entity.Place;
import com.meetmate.place.exception.NotFoundException;
import com.meetmate.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public PlaceResponse createPlace(CreatePlaceRequest request) {
        Place place = Place.builder()
            .name(request.getName())
            .category(request.getCategory())
            .area(request.getArea())
            .address(request.getAddress())
            .lat(request.getLat())
            .lng(request.getLng())
            .costLevel(request.getCostLevel())
            .hasVeg(request.isHasVeg())
            .hasNonVeg(request.isHasNonVeg())
            .rating(request.getRating())
            .isActive(true)
            .build();
        Place saved = placeRepository.save(place);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PlaceResponse> listPlaces(String category, String area, String costLevel) {
        return placeRepository.searchActive(category, area, costLevel)
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlaceResponse getPlace(UUID id) {
        Place place = placeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Place not found"));
        return toResponse(place);
    }

    @Transactional
    public PlaceResponse updatePlace(UUID id, UpdatePlaceRequest request) {
        Place place = placeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Place not found"));

        if (request.getName() != null) place.setName(request.getName());
        if (request.getCategory() != null) place.setCategory(request.getCategory());
        if (request.getArea() != null) place.setArea(request.getArea());
        if (request.getAddress() != null) place.setAddress(request.getAddress());
        if (request.getLat() != null) place.setLat(request.getLat());
        if (request.getLng() != null) place.setLng(request.getLng());
        if (request.getCostLevel() != null) place.setCostLevel(request.getCostLevel());
        if (request.getHasVeg() != null) place.setHasVeg(request.getHasVeg());
        if (request.getHasNonVeg() != null) place.setHasNonVeg(request.getHasNonVeg());
        if (request.getRating() != null) place.setRating(request.getRating());
        if (request.getActive() != null) place.setActive(request.getActive());

        Place saved = placeRepository.save(place);
        return toResponse(saved);
    }

    @Transactional
    public void deletePlace(UUID id) {
        Place place = placeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Place not found"));
        place.setActive(false);
        placeRepository.save(place);
    }

    private PlaceResponse toResponse(Place place) {
        return PlaceResponse.builder()
            .id(place.getId())
            .name(place.getName())
            .category(place.getCategory())
            .area(place.getArea())
            .address(place.getAddress())
            .lat(place.getLat())
            .lng(place.getLng())
            .costLevel(place.getCostLevel())
            .hasVeg(place.isHasVeg())
            .hasNonVeg(place.isHasNonVeg())
            .rating(place.getRating())
            .active(place.isActive())
            .build();
    }
}

