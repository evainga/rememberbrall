package de.rememberbrall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "de.rememberbrall") //ask Marco or test without
public class RememberbrallApplication {

    public static void main(String[] args) {
        SpringApplication.run(RememberbrallApplication.class, args);
    }
}
