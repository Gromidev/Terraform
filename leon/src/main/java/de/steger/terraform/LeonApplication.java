package de.steger.terraform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main-Klasse der Anwendung
 */
@SpringBootApplication
public class LeonApplication {

	public static void main(String[] args) {
		// Übergibt an Spring zum Starten des Web-Servers
		SpringApplication.run(LeonApplication.class, args);
	}

}
