package de.rememberbrall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RememberbrallApplication.class)
@AutoConfigureWebTestClient
public class ThymeleafControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void showAllEventsStatusCode() {
        webTestClient
                .get()
                .uri("/thymeleaf-entries")
                .exchange()
                .expectStatus()
                .isOk();
    }
}
