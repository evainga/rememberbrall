package de.rememberbrall;

import static io.restassured.RestAssured.given;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@AutoConfigureWebTestClient
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RememberbrallApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ThymeleafControllerIT extends AbstractTestNGSpringContextTests {


    @Value("${local.server.port}")
    private int port;

    public RequestSpecification getPlainRequestSpec() {
        return new RequestSpecBuilder()
                .build()
                .baseUri("http://localhost")
                .port(port);
    }

    @Test
    public void showAllEventsStatusCode() {
        given(getPlainRequestSpec())
                .when()
                .get("thymeleaf-entries")
                .then()
                .statusCode(200);
    }
}
