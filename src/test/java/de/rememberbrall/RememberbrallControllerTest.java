package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class RememberbrallControllerTest {

    @InjectMocks
    private RememberbrallController rememberbrallController;
    @Mock
    private RememberbrallService rememberbrallService;
    @Mock
    private Entry entry;

    @BeforeTest
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void showAllEntries() {
        //given
        when(rememberbrallService.getAllEntries()).thenReturn(Arrays.asList(entry, entry, entry));

        //when
        List<Entry> newList = rememberbrallController.showAllEntries();

        //then
        assertThat(newList).isNotEmpty();
        assertThat(newList).hasSize(3);
        assertThat(newList).hasOnlyElementsOfType(Entry.class);
    }

    @Test
    public void showSpecificExistingEntry() throws MalformedURLException {
        //given
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Entry entry = new Entry(uuid, "name", EntryCategory.ENTWICKLUNG, new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
        when(rememberbrallService.getEntryByUUID(uuid)).thenReturn(Optional.of(entry));

        //when
        ResponseEntity<Entry> specificEntry = rememberbrallController.showSpecificEntry(uuid);

        //then
        assertThat(specificEntry.getBody()).isEqualTo(entry);
    }

    @Test
    public void showSpecificNonExistingEntry() throws MalformedURLException {
        //given
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(rememberbrallService.getEntryByUUID(uuid)).thenReturn(Optional.empty());

        //when
        ResponseEntity<Entry> specificEntry = rememberbrallController.showSpecificEntry(uuid);

        //then
        assertThat(specificEntry.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createEntry() {
        //TODO
    }

    @Test
    public void deleteEntry() {
        //TODO
    }

}
