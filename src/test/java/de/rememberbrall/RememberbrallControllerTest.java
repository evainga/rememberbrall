package de.rememberbrall;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class RememberbrallControllerTest {

    private RememberbrallController rememberbrallController;

    private RememberbrallService rememberbrallService = mock(RememberbrallService.class);

    private Entry entry;
    private Entry entryWithId;


    private static final String ID_EXAMPLE = "00000000-0000-0000-0000-000000000001";

    @BeforeTest
    public void init() throws MalformedURLException {
        rememberbrallController = new RememberbrallController(rememberbrallService);
        entry = new Entry("Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
        entryWithId = new Entry(ID_EXAMPLE, "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
    }

    @Test
    public void showAllEntries() {
        //given
        when(rememberbrallService.getAllEntries()).thenReturn(Flux.just(entry, entry, entry));

        //when
        Flux<Entry> allEntries = rememberbrallController.showAllEntries();

        //then
        StepVerifier.create(allEntries)
                .expectNext(entry, entry, entry)
                .verifyComplete();
    }

    @Test
    public void showSpecificExistingEntry() {
        //given
        when(rememberbrallService.getEntryByID(ID_EXAMPLE)).thenReturn(Mono.just(entryWithId));

        //when
        Mono<ResponseEntity<Entry>> specificEntry = rememberbrallController.showSpecificEntry(ID_EXAMPLE);

        //then
        StepVerifier.create(specificEntry)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode() == (HttpStatus.OK)
                                && Objects.requireNonNull(responseEntity.getBody()).getId().equals(ID_EXAMPLE))
                .verifyComplete();
    }

    @Test
    public void showSpecificNonExistingEntry() {
        //given
        when(rememberbrallService.getEntryByID(ID_EXAMPLE)).thenReturn(Mono.empty());

        //when
        Mono<ResponseEntity<Entry>> specificEntry = rememberbrallController.showSpecificEntry(ID_EXAMPLE);

        //then
        StepVerifier.create(specificEntry)
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                .verifyComplete();

    }

    @Test
    public void createEntry() {
        //given
        when(rememberbrallService.createEntry(entry)).thenReturn(Mono.just(entryWithId));

        //when
        Mono<ResponseEntity<Entry>> newEntry = rememberbrallController.createEntry(entry);

        //then
        StepVerifier.create(newEntry)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode() == (HttpStatus.CREATED) &&
                                Objects.requireNonNull(responseEntity.getHeaders().getLocation())
                                        .getPath().equals(ID_EXAMPLE))
                .verifyComplete();
    }

    @Test
    public void deleteEntry() {
        //given
        when(rememberbrallService.deleteEntry(ID_EXAMPLE)).thenReturn(Mono.empty());
        //when
        Mono<ResponseEntity<Void>> deleteEntry = rememberbrallController.deleteEntry(ID_EXAMPLE);
        //then
        StepVerifier.create(deleteEntry)
                .verifyComplete();

    }

    @Test
    public void deleteAllEntries() {
        //given
        when(rememberbrallService.deleteAllEntries()).thenReturn(Mono.empty());
        //when
        Mono<ResponseEntity<Void>> deleteAllEntries = rememberbrallController.deleteAllEntries();
        //then
        StepVerifier.create(deleteAllEntries)
                .verifyComplete();
    }

    @Test
    public void updateEntry() throws MalformedURLException {
        //given
        when(rememberbrallService.updateEntry(ID_EXAMPLE, new Entry("New Entry Name", EntryCategory.LINUX, new URL("http://www.new-url.de")))).thenReturn(Mono.just(entry));

        //when
        Mono<ResponseEntity<Entry>> updatedEntry = rememberbrallController.updateEntry(ID_EXAMPLE, new Entry("New Entry Name", EntryCategory.LINUX, new URL("http://www.new-url.de")));

        //then
        StepVerifier.create(updatedEntry)
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    public void updateNonExistingEntry() throws MalformedURLException {
        //given
        when(rememberbrallService.updateEntry(ID_EXAMPLE, new Entry("New Entry Name", EntryCategory.LINUX, new URL("http://www.new-url.de")))).thenReturn(Mono.empty());

        //when
        Mono<ResponseEntity<Entry>> updatedEntry = rememberbrallController.updateEntry(ID_EXAMPLE, new Entry("New Entry Name", EntryCategory.LINUX, new URL("http://www.new-url.de")));

        //then
        StepVerifier.create(updatedEntry)
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                .verifyComplete();
    }

}
