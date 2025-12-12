package com.meetmate.place.controller;

import com.meetmate.place.dto.request.CreatePlaceRequest;
import com.meetmate.place.dto.request.UpdatePlaceRequest;
import com.meetmate.place.dto.response.PlaceResponse;
import com.meetmate.place.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceResponse create(@Valid @RequestBody CreatePlaceRequest request) {
        return placeService.createPlace(request);
    }

    @GetMapping
    public List<PlaceResponse> list(@RequestParam(required = false) String category,
                                    @RequestParam(required = false) String area,
                                    @RequestParam(required = false) String costLevel) {
        return placeService.listPlaces(category, area, costLevel);
    }

    @GetMapping("/{id}")
    public PlaceResponse get(@PathVariable UUID id) {
        return placeService.getPlace(id);
    }

    @PutMapping("/{id}")
    public PlaceResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePlaceRequest request) {
        return placeService.updatePlace(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        placeService.deletePlace(id);
    }
}

