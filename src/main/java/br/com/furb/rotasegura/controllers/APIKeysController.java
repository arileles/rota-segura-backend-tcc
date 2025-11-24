package br.com.furb.rotasegura.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/v1/keys")
@RestController
public class APIKeysController {

    @GetMapping("/maps")
    public String getMapKey(@Value("${google.map.key}") String key) {
        return key;
    }    
}
