package de.rememberbrall;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.reactive.function.client.WebClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;
import reactor.core.publisher.Flux;

@SpringBootTest(classes = RememberbrallApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RememberbrallControllerDocumentation extends AbstractTestNGSpringContextTests {

    private static final String LINUX_WASCHMITTEL = "Linux Waschmittel";

    private ManualRestDocumentation restDocumentation = new ManualRestDocumentation("target/generated-snippets");

    @Value("${local.server.port}")
    private int port;

    public RequestSpecification getPlainRequestSpec() {
        return new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation).snippets().withEncoding("UTF-8"))
                .build()
                .baseUri("http://localhost")
                .port(port);
    }

    @Test
    public void showAllEntriesReactive() {
        WebClient client = WebClient.create("http://localhost:" + port);
        Flux<Entry> result = client
                .get()
                .uri("/entries")
                .accept(APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(Entry.class);
        System.out.println("==== ");
        result.subscribe(System.out::println);
        // assertThat(1).isEqualTo(2);
    }

    @Test(enabled = false)
    public void showAllEntries() {
        given(getPlainRequestSpec())
                .filter(document("show-entries",
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("[0].id").description("The ID of an entry"),
                                fieldWithPath("[0].name").description("The name of an entry"),
                                fieldWithPath("[0].category").description("The category an entry can be associated with"),
                                fieldWithPath("[0].url").description("The absolute URL of an entry"))))
                .when()
                .get("entries")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].id", both(instanceOf(String.class)).and(not("")))
                .body("[0].name", both(instanceOf(String.class)).and(not("")))
                .body("[0].category", both(instanceOf(String.class)).and(not("")))
                .body("[0].url", both(instanceOf(String.class)).and(not("")));
    }

    @Test
    public void createEntry() throws MalformedURLException {
        given(getPlainRequestSpec())
                .filter(document("create-entry",
                        preprocessRequest(prettyPrint()),
                        requestFields(fieldWithPath("id").description("The ID of an entry"),
                                fieldWithPath("name").description("The given name of the entry"),
                                fieldWithPath("category").description("The given name of the entry"),
                                fieldWithPath("url").description("The given name of the entry"))))
                .when()
                .body(new Entry(LINUX_WASCHMITTEL, EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)")))
                .contentType(ContentType.JSON)
                .post("entries")
                .then()
                .statusCode(201)
                .header(HttpHeaders.LOCATION, not(empty()));
    }

    @Test
    public void createAndShowSpecificEntry() throws MalformedURLException {
        String locationHeader = getLocationHeaderForCreatedEntry();

        given(getPlainRequestSpec())
                .filter(document("show-specific-entry",
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("id").description("The ID of an entry"),
                                fieldWithPath("name").description("The name of an entry"),
                                fieldWithPath("category").description("The category an entry can be associated with"),
                                fieldWithPath("url").description("The absolute URL of an entry"))))
                .accept(ContentType.JSON)
                .when()
                .get("entries/{id}", locationHeader)
                .then()
                .statusCode(200)
                .body("id", any(String.class))
                .body("name", is(LINUX_WASCHMITTEL))
                .body("category", is("LINUX"))
                .body("url", is("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));
    }

    @Test
    public void deleteNonExistingEntry() {
        given(getPlainRequestSpec())
                .filter(document("delete-non-existing-entry"))
                .when()
                .delete("entries/{id}", "dummyId")
                .then()
                .statusCode(204);
    }

    @Test
    public void deleteNewlyCreatedEntry() throws MalformedURLException {
        String locationHeader = getLocationHeaderForCreatedEntry();

        given(getPlainRequestSpec())
                .filter(document("delete-newly-created-entry"))
                .when()
                .delete("entries/{id}", locationHeader)
                .then()
                .statusCode(204);

        given(getPlainRequestSpec())
                .when()
                .get("entries/{id}", locationHeader)
                .then()
                .statusCode(404);
    }

    private String getLocationHeaderForCreatedEntry() throws MalformedURLException {
        Entry entry = new Entry(LINUX_WASCHMITTEL, EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));
        return given(getPlainRequestSpec())
                .when()
                .body(entry)
                .contentType(ContentType.JSON)
                .post("entries")
                .then()
                .statusCode(201)
                .extract()
                .header(HttpHeaders.LOCATION);
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
