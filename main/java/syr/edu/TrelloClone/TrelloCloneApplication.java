package syr.edu.TrelloClone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "syr.edu.TrelloClone")
public class TrelloCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrelloCloneApplication.class, args);
	}

}
