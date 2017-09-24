package de.rememberbrall;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
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
                        responseFields(fieldWithPath("[0].entryId").description("The UUID of an entry"),
                                fieldWithPath("[0].entryName").description("The name of an entry"),
                                fieldWithPath("[0].entryCategory").description("The category an entry can be associated with"),
                                fieldWithPath("[0].entryUrl").description("The absolute URL of an entry"))))
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

    @Test
    public void showSpecificEntry() {
        given(getPlainRequestSpec())
                .filter(document("show-specific-entry",
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("entryId").description("The UUID of an entry"),
                                fieldWithPath("entryName").description("The given name of the entry"),
                                fieldWithPath("entryCategory").description("The given name of the entry"),
                                fieldWithPath("entryUrl").description("The given name of the entry"))))
                .accept(ContentType.JSON)
                .when()
                .get("entries/{uuid}", UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .then()
                .statusCode(200)
                .body("entryId", is("00000000-0000-0000-0000-000000000002"))
                .body("entryName", is("4 Techniques for Writing better Java"))
                .body("entryCategory", is("JAVA"))
                .body("entryUrl", is("https://dzone.com/articles/4-techniques-for-writing-better-java"));
    }

    @Test
    public void createEntry() throws MalformedURLException {
        given(getPlainRequestSpec())
                .filter(document("create-entry",
                        preprocessRequest(prettyPrint()),
                        requestFields(fieldWithPath("entryId").description("The UUID of an entry"),
                                fieldWithPath("entryName").description("The given name of the entry"),
                                fieldWithPath("entryCategory").description("The given name of the entry"),
                                fieldWithPath("entryUrl").description("The given name of the entry"))))
                .when()
                .body(new Entry("LINUX", EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)")))
                .contentType(ContentType.JSON)
                .post("entries")
                .then()
                .statusCode(201)
                .header(HttpHeaders.LOCATION, not(empty()));

    }

    @Test
    public void deleteNonExistingEntry() {
        given(getPlainRequestSpec())
                .filter(document("delete-non-existing-entry"))
                .when()
                .get("entries/{uuid}", UUID.fromString("00000000-0000-0000-0000-000000000404"))
                .then()
                .statusCode(404);
    }

    @Test
    public void deleteNewlyCreatedEntry() throws MalformedURLException {
        String uuid = given(getPlainRequestSpec())
                .when()
                .body(new Entry("LINUX", EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)")))
                .contentType(ContentType.JSON)
                .post("entries")
                .then()
                .statusCode(201)
                .extract()
                .header(HttpHeaders.LOCATION);

        given(getPlainRequestSpec())
                .filter(document("delete-newly-created-entry"))
                .when()
                .delete("entries/{uuid}", uuid)
                .then()
                .statusCode(204);
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
