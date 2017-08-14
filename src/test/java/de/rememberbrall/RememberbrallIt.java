package de.rememberbrall;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(classes = RememberbrallApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RememberbrallIt extends AbstractTestNGSpringContextTests {

    @Value("${local.server.port}")
    int port;

    public RequestSpecification getPlainRequestSpec() {
        return new RequestSpecBuilder().build().baseUri("http://localhost/rememberbrall").port(port);
    }

    @Test
    public void showAllEntrys() {
        given(getPlainRequestSpec())
                .when()
                .get("entrys")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].entrytId", both(instanceOf(String.class)).and(not("")))
                .body("[0].entryName", both(instanceOf(String.class)).and(not("")))
                .body("[0].entryCateory", both(instanceOf(String.class)).and(not("")))
                .body("[0].entryUrl", both(instanceOf(String.class)).and(not("")));

    }
}
