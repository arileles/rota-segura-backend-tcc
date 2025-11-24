package br.com.furb.rotasegura.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import br.com.furb.rotasegura.domain.records.GeocodeResult;

@RestController
@RequestMapping("/v1/geo")
public class GeocodingController {

    @Autowired
    private GeoApiContext geoApiContext;

    @GetMapping("/address")
    public ResponseEntity<GeocodeResult> searchAddress(@RequestParam String q) throws ApiException, InterruptedException, IOException {

        GeocodingResult[] response = GeocodingApi.newRequest(geoApiContext).address(q).await();
        if (response == null || response.length == 0) {
            return ResponseEntity.notFound().build();
        }

        LatLng l = response[0].geometry.location;
        return ResponseEntity.ok(new GeocodeResult(l.lat, l.lng, response[0].formattedAddress));
    }
}
