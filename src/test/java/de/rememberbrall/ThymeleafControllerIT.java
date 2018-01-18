package de.rememberbrall;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static de.rememberbrall.RememberbrallControllerDocumentation.LINUX_WASCHMITTEL;
import static io.restassured.RestAssured.given;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import static org.hamcrest.CoreMatchers.containsString;

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
    public void showAllEvents() {
        given(getPlainRequestSpec())
                .when()
                .get("thymeleaf-entries")
                .then()
                .statusCode(200)
                .content(containsString("Rememberbrall Entries"))
                .content(containsString(LINUX_WASCHMITTEL));
    }
}
