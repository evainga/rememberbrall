package de.rememberbrall;

import static de.rememberbrall.SecurityConfig.ADMIN;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RememberbrallApplication.class)
@AutoConfigureWebTestClient
public class RememberbrallControllerDocumentation {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    ApplicationContext context;

    private WebTestClient webTestClient;

    private static final String LINUX_WASCHMITTEL = "Linux Waschmittel";
    private Entry entry;
    private Entry newEntry;


    @Before
    public void setup() throws MalformedURLException {
        entry = new Entry(LINUX_WASCHMITTEL, EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));
        newEntry = new Entry("New Entry Name", EntryCategory.JAVA, new URL("http://www.new-url.de"));

        this.webTestClient = WebTestClient
                .bindToApplicationContext(context)
                .apply(SecurityMockServerConfigurers.springSecurity())
                .configureClient()
                .filter(documentationConfiguration(restDocumentation).snippets().withEncoding("UTF-8"))
                .filter(basicAuthentication())
                .build()
                .mutateWith(csrf());
    }

    @Test
    public void createEntry() {
        webTestClient
                .post()
                .uri("/entries")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(entry), Entry.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .valueMatches(HttpHeaders.LOCATION, "^((?!null).)+$")
                .expectBody()
                .consumeWith(document("create-entry",
                        preprocessRequest(prettyPrint()),
                        requestFields(fieldWithPath("id").description("The ID of an entry"),
                                fieldWithPath("name").description("The given name of the entry"),
                                fieldWithPath("category").description("The given name of the entry"),
                                fieldWithPath("url").description("The given name of the entry"))));
    }

    @Test
    public void getSpecificEntry() {
        String idForCreatedEntry = getIdForCreatedEntry();

        webTestClient
                .get()
                .uri("/entries/" + idForCreatedEntry)
                .exchange()
                .expectBody().consumeWith(document("get-specific-entry",
                preprocessResponse(prettyPrint()),
                responseFields(fieldWithPath("id").description("The ID of an entry"),
                        fieldWithPath("name").description("The name of an entry"),
                        fieldWithPath("category").description("The category an entry can be associated with"),
                        fieldWithPath("url").description("The absolute URL of an entry"))))
                .jsonPath("id").exists()
                .jsonPath("name").isEqualTo(LINUX_WASCHMITTEL)
                .jsonPath("category").isEqualTo("LINUX")
                .jsonPath("url").isEqualTo("https://de.wikipedia.org/wiki/Linux_(Waschmittel)");
    }


    @Test
    public void updateSpecificEntry() {
        String idForCreatedEntry = getIdForCreatedEntry();

        webTestClient
                .put()
                .uri("/entries/" + idForCreatedEntry)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(newEntry), Entry.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(document("update-specific-entry",
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("id").description("The ID of an entry"),
                                fieldWithPath("name").description("The name of an entry"),
                                fieldWithPath("category").description("The category an entry can be associated with"),
                                fieldWithPath("url").description("The absolute URL of an entry"))))
                .jsonPath("id").exists()
                .jsonPath("name").isEqualTo("New Entry Name")
                .jsonPath("category").isEqualTo("JAVA")
                .jsonPath("url").isEqualTo("http://www.new-url.de");
    }

    @Test
    public void getAllEntries() {
        deleteAllEntriesWithAdminRights();

        getIdForCreatedEntry();
        getIdForCreatedEntry();

        webTestClient
                .get()
                .uri("/entries")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Entry.class)
                .hasSize(2)
                .consumeWith(document("get-all-entries",
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("[0].id").description("The ID of an entry"),
                                fieldWithPath("[0].name").description("The name of an entry"),
                                fieldWithPath("[0].category").description("The category an entry can be associated with"),
                                fieldWithPath("[0].url").description("The absolute URL of an entry"))));
    }

    @Test
    public void getAllEntriesReactiveShowCase() {
        deleteAllEntriesWithAdminRights();

        getIdForCreatedEntry();
        getIdForCreatedEntry();

        Flux<Entry> allEntries = webTestClient
                .get()
                .uri("/entries")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType("application/stream+json;charset=UTF-8")
                .returnResult(Entry.class)
                .getResponseBody();

        StepVerifier.create(allEntries)
                .expectNext(entry, entry)
                .verifyComplete();
    }

    @Test
    public void deleteSingleEntry() {

        String idForCreatedEntry = getIdForCreatedEntry();

        webTestClient
                .delete()
                .uri("/entries/" + idForCreatedEntry)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .consumeWith(document("delete-created-entry"));

        webTestClient
                .delete()
                .uri("/entries/" + idForCreatedEntry)
                .exchange()
                .expectStatus()
                .isNoContent(); //TODO: Make it more concrete with Not Found
    }

    @Test
    public void deleteNonExistingEntry() {
        webTestClient
                .delete()
                .uri("/entries/" + "non-existing-id")
                .exchange()
                .expectStatus()
                .isNoContent()//TODO: Make it more concrete with Not Found
                .expectBody()
                .consumeWith(document("delete-non-existing-entry"));
    }

    @Test
    public void deleteAllEntriesWithAdminRights() {
        getIdForCreatedEntry();

        webTestClient
                .mutateWith(mockUser().roles(ADMIN))
                .delete()
                .uri("/entries")
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .consumeWith(document("delete-all-entries"));
    }

    @Test
    public void forbidToDeleteAllEntriesWithNoAdminRights() {
        getIdForCreatedEntry();

        webTestClient
                .delete()
                .uri("/entries")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    public String getIdForCreatedEntry() {
        return Objects.requireNonNull(
                webTestClient
                        .post()
                        .uri("/entries")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(Mono.just(entry), Entry.class)
                        .exchange()
                        .returnResult(Entry.class)
                        .getResponseHeaders().getLocation())
                .toString();
    }
}
