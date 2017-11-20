package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import reactor.core.publisher.Flux;

public class RememberbrallServiceTest {

    private RememberbrallService rememberbrallService = new RememberbrallService();

    @Before
    public void setupDb() throws MalformedURLException {
        rememberbrallService.setupDb();
    }

    @Test
    public void getAllEntries() {
        assertThat(rememberbrallService.getAllEntries()).extracting(entryFlux -> true).hasAtLeastOneElementOfType(Entry.class);
    }

    @Test
    public void getFirstEntry() {
        Flux<Entry> allEvents = rememberbrallService.getAllEntries();
        assertThat(allEvents.get(0).getEntryId()).isInstanceOf(UUID.class);
        assertThat(allEvents.get(0).getEntryName()).isInstanceOf(String.class);
        assertThat(allEvents.get(0).getEntryCategory()).isInstanceOf(EntryCategory.class);
        assertThat(allEvents.get(0).getEntryUrl()).isInstanceOf(URL.class);
    }

    @Test
    public void getEntryByUUID() throws MalformedURLException {
        Entry entry = new Entry(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
        Optional<Entry> existingEntry = rememberbrallService.getEntryByUUID(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Optional<Entry> nonExistingEntry = rememberbrallService.getEntryByUUID(UUID.fromString("40400000-0000-0000-0000-000000000000"));

        assertThat(existingEntry.get()).isEqualTo(entry);
        assertThat(nonExistingEntry).isNotIn(rememberbrallService); // ask Marco if possible?
        assertThat(nonExistingEntry).isEqualTo(Optional.empty());
    }

    @Test
    public void createEntry() throws MalformedURLException {

        Entry testEntry = new Entry(UUID.fromString("4414177a-8b5b-4e1f-8fe8-eb736f39ce13"), "LINUX",
                EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));

        UUID newUuid = rememberbrallService.createEntry(testEntry);
        List<Entry> allEntries = rememberbrallService.getAllEntries();

        assertThat(newUuid).isNotNull();
        assertThat(allEntries).contains(testEntry);

    }

    @Test // get review from Marco
    public void deleteEntry() throws MalformedURLException {

        assertThat(rememberbrallService.deleteEntry(UUID.fromString("00000000-0000-0000-0000-000000000001"))).isTrue();
        assertThat(rememberbrallService.deleteEntry(UUID.fromString("00000000-0000-0000-0000-000000000404"))).isFalse();

    }
}
