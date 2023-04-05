package skuniv.capstone;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CapstoneApplication implements WebMvcConfigurer {
	// CORS 설정
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*");
	}
	public static void main(String[] args) {
		SpringApplication.run(CapstoneApplication.class, args);
	}
//	@Bean
	Hibernate5JakartaModule hibernate5Module() {
		return new Hibernate5JakartaModule();
	}
}
