package com.utc2.facility;

import com.utc2.facility.configuration.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class FacilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacilityApplication.class, args);
	}

}
