package br.com.furb.rotasegura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RotaseguraBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RotaseguraBackendApplication.class, args);
	}

}
