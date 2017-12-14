package de.rememberbrall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "de.rememberbrall")
@EnableReactiveMongoRepositories
//@PropertySource("application-${spring.profiles.active}.properties")
public class RememberbrallApplication {

    public static void main(String[] args) {
        SpringApplication.run(RememberbrallApplication.class, args);
    }
}
