package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RememberbrallServiceTest extends MockitoTest {

    @InjectMocks
    private RememberbrallService rememberbrallService;

    @Mock
    private EntryRepository entryRepository;

    @Test
    public void getAllEntries() {
        assertThat(rememberbrallService.getAllEntries().buffer().blockLast()).hasSize(4);
    }

    @Test
    public void getFirstEntry() {
        Flux<Entry> allEvents = rememberbrallService.getAllEntries();
        Entry entry = allEvents.buffer().blockLast().get(0);
        assertThat(entry.getId()).isInstanceOf(UUID.class);
        assertThat(entry.getName()).isInstanceOf(String.class);
        assertThat(entry.getCategory()).isInstanceOf(EntryCategory.class);
        assertThat(entry.getUrl()).isInstanceOf(URL.class);
    }

    @Test
    public void getEntryByUUID() throws MalformedURLException {
        //Given
        Entry entry = new Entry("00000000-0000-0000-0000-000000000001", "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
        when(entryRepository.findById("00000000-0000-0000-0000-000000000001")).thenReturn(Mono.just(entry));
        //When
        Mono<Entry> existingEntry = rememberbrallService.getEntryByUUID("00000000-0000-0000-0000-000000000001");

        //Then
        assertThat(existingEntry.block()).isEqualTo(entry);
    }

    @Test
    public void createEntry() throws MalformedURLException {

        Entry testEntry = new Entry("4414177a-8b5b-4e1f-8fe8-eb736f39ce13", "LINUX",
                EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));

        Mono<Entry> newEntry = rememberbrallService.createEntry(testEntry);

        assertThat(newEntry).isNotNull();

    }
}
