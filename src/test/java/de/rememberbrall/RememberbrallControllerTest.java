package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RememberbrallControllerTest {

    @InjectMocks
    private RememberbrallController rememberbrallController;
    @Mock
    private RememberbrallService rememberbrallService;
    @Mock
    private Entry entry;

    private static final String ID_EXAMPLE = "00000000-0000-0000-0000-000000000002";

    @BeforeTest
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void showAllEntries() {
        //given
        when(rememberbrallService.getAllEntries()).thenReturn(Flux.just(entry, entry, entry));

        //when
        Flux<Entry> newFlux = rememberbrallController.showAllEntries();

        //then
        assertThat(newFlux.buffer().blockLast()).isNotEmpty();
        assertThat(newFlux.buffer().blockLast()).hasSize(3);
        assertThat(newFlux.buffer().blockLast()).hasOnlyElementsOfType(Entry.class);
    }

    @Test
    public void showSpecificExistingEntry() throws MalformedURLException {
        //given
        when(rememberbrallService.getEntryByID(ID_EXAMPLE)).thenReturn(Mono.just(entry));

        //when
        ResponseEntity<Entry> specificEntry = rememberbrallController.showSpecificEntry(ID_EXAMPLE);

        //then
        assertThat(specificEntry.getBody()).isEqualTo(entry);
    }

    @Test
    public void showSpecificNonExistingEntry() throws MalformedURLException {
        //given
        when(rememberbrallService.getEntryByID(ID_EXAMPLE)).thenReturn(Mono.empty());

        //when
        ResponseEntity<Entry> specificEntry = rememberbrallController.showSpecificEntry(ID_EXAMPLE);

        //then
        assertThat(specificEntry.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createEntry() {
        //given

        when(rememberbrallService.createEntry(entry)).thenReturn(Mono.just(entry));
        //when
        ResponseEntity<Entry> newEntry = rememberbrallController.createEntry(entry);
        //then
        assertThat(newEntry.getStatusCode()).isSameAs(HttpStatus.CREATED);
        assertThat(newEntry.getHeaders().getLocation().toString()).isEqualTo(ID_EXAMPLE);
    }

    @Test
    public void deleteEntry() {
        //given
        when(rememberbrallService.deleteEntry(ID_EXAMPLE)).thenReturn(Mono.empty());
        //when
        ResponseEntity<?> deleteEntry = rememberbrallController.deleteEntry(ID_EXAMPLE);
        //then
        assertThat(deleteEntry.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
