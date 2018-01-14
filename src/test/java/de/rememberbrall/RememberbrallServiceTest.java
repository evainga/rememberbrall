package de.rememberbrall;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RememberbrallServiceTest extends MockitoTest {

    private Entry entry;

    @InjectMocks
    private RememberbrallService rememberbrallService;

    @Mock
    private EntryRepository entryRepository;


    @BeforeTest
    public void createInitialEntry() throws MalformedURLException {
        entry = new Entry("00000000-0000-0000-0000-000000000001", "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
    }


    @Test
    public void getAllEntries() throws MalformedURLException {
        //given
        Entry entry2 = new Entry("00000000-0000-0000-0000-000000000002", "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));

        List<Entry> entryList = new ArrayList<>();
        entryList.add(entry);
        entryList.add(entry2);
        Flux<Entry> entryFlux = Flux.fromIterable(entryList);
        when(entryRepository.findAll()).thenReturn(entryFlux);

        //when
        Flux<Entry> allEntries = rememberbrallService.getAllEntries();

        //then
        assertThat(allEntries.buffer().blockLast()).hasSize(2);
        assertThat(allEntries.buffer().blockLast()).hasOnlyElementsOfType(Entry.class);

        Entry firstEntry = allEntries.buffer().blockLast().get(0);
        assertThat(firstEntry.getId()).isInstanceOf(String.class);
        assertThat(firstEntry.getName()).isInstanceOf(String.class);
        assertThat(firstEntry.getCategory()).isInstanceOf(EntryCategory.class);
        assertThat(firstEntry.getUrl()).isInstanceOf(URL.class);
    }

    @Test
    public void getEntryByID() {
        //given
        when(entryRepository.findById("00000000-0000-0000-0000-000000000001")).thenReturn(Mono.just(entry));

        //when
        Mono<Entry> existingEntry = rememberbrallService.getEntryByID("00000000-0000-0000-0000-000000000001");

        //then
        assertThat(existingEntry.block()).isEqualTo(entry);
    }

    @Test
    public void createEntry() {
        //given
        Mono<Entry> newMonoEntry = Mono.just(entry);
        when(entryRepository.insert(entry)).thenReturn(newMonoEntry);

        //when
        Mono<Entry> newEntry = rememberbrallService.createEntry(entry);

        //then
        assertThat(newEntry).isNotNull();
    }
}
