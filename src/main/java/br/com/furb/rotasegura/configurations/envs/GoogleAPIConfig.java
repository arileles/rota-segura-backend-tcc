package br.com.furb.rotasegura.configurations.envs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.maps.GeoApiContext;

import jakarta.annotation.PreDestroy;

@Configuration
public class GoogleAPIConfig {

    @Bean
    GeoApiContext geoApiContext(@Value("${google.api.key}") String googleApiKey) {
        return new GeoApiContext.Builder()
                .apiKey(googleApiKey)
                .build();
    }
}
