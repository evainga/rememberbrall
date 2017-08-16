package de.rememberbrall;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(classes = RememberbrallApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RememberbrallDocumentation extends AbstractTestNGSpringContextTests {

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation("target/generated-snippets");

    @Value("${local.server.port}")
    int port;

    public RequestSpecification getPlainRequestSpec() {
        return new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation).snippets().withEncoding("UTF-8"))
                .build()
                .baseUri("http://localhost")
                .port(port);
    }

    @Test
    public void showAllEntries() {
        given(getPlainRequestSpec())
                .filter(document("show-entries",
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("[0].entryId").description(""),
                                fieldWithPath("[0].entryName").description(""),
                                fieldWithPath("[0].entryCategory").description(""),
                                fieldWithPath("[0].entryUrl").description(""))))
                .accept(ContentType.JSON)
                .when()
                .get("entries")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].entryId", both(instanceOf(String.class)).and(not("")))
                .body("[0].entryName", both(instanceOf(String.class)).and(not("")))
                .body("[0].entryCategory", both(instanceOf(String.class)).and(not("")))
                .body("[0].entryUrl", both(instanceOf(String.class)).and(not("")));
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        restDocumentation.beforeTest(getClass(), method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        restDocumentation.afterTest();
    }
}
