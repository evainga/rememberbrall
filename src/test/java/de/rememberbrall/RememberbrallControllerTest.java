package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.util.Optional;
import java.util.UUID;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import reactor.core.publisher.Flux;

public class RememberbrallControllerTest {

    @InjectMocks
    private RememberbrallController rememberbrallController;
    @Mock
    private RememberbrallService rememberbrallService;
    @Mock
    private Entry entry;

    private static final UUID UUID_EXAMPLE = UUID.fromString("00000000-0000-0000-0000-000000000002");

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
        when(rememberbrallService.getEntryByUUID(UUID_EXAMPLE)).thenReturn(Optional.of(entry));

        //when
        ResponseEntity<Entry> specificEntry = rememberbrallController.showSpecificEntry(UUID_EXAMPLE);

        //then
        assertThat(specificEntry.getBody()).isEqualTo(entry);
    }

    @Test
    public void showSpecificNonExistingEntry() throws MalformedURLException {
        //given
        when(rememberbrallService.getEntryByUUID(UUID_EXAMPLE)).thenReturn(Optional.empty());

        //when
        ResponseEntity<Entry> specificEntry = rememberbrallController.showSpecificEntry(UUID_EXAMPLE);

        //then
        assertThat(specificEntry.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createEntry() {
        //given
        when(rememberbrallService.createEntry(entry)).thenReturn(UUID_EXAMPLE);
        //when
        ResponseEntity<Entry> newEntry = rememberbrallController.createEntry(entry);
        //then
        assertThat(newEntry.getStatusCode()).isSameAs(HttpStatus.CREATED);
        assertThat(newEntry.getHeaders().getLocation().toString()).isEqualTo(UUID_EXAMPLE.toString());
    }

    @Test
    public void deleteExistingEntry() {
        //given
        when(rememberbrallService.deleteEntry(UUID_EXAMPLE)).thenReturn(true);
        //when
        ResponseEntity<?> deleteEntry = rememberbrallController.deleteEntry(UUID_EXAMPLE);
        //then
        assertThat(deleteEntry.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void deleteNonExistingEntry() {
        //given
        when(rememberbrallService.deleteEntry(UUID_EXAMPLE)).thenReturn(false);
        //when
        ResponseEntity<?> deleteEntry = rememberbrallController.deleteEntry(UUID_EXAMPLE);
        //then
        assertThat(deleteEntry.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
