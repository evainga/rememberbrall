package de.rememberbrall;

import java.net.MalformedURLException;
import java.net.URL;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class RememberbrallServiceTest extends MockitoTest {

    private Entry entry;
    private Entry entry2;

    @InjectMocks
    private RememberbrallService rememberbrallService;

    @Mock
    private EntryRepository entryRepository;


    @BeforeTest
    public void createInitialEntry() throws MalformedURLException {
        entry = new Entry("00000000-0000-0000-0000-000000000001", "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
        entry2 = new Entry("00000000-0000-0000-0000-000000000002", "Reactive Testing", EntryCategory.ENTWICKLUNG,
                new URL("http://projectreactor.io/docs/core/release/reference/docs/index.html#testing"));
    }


    @Test
    public void getAllEntriesValidOrder() throws MalformedURLException {
        //given
        Flux<Entry> entryFlux = Flux.just(entry, entry2);
        when(entryRepository.findAll()).thenReturn(entryFlux);

        //when
        Flux<Entry> allEntries = rememberbrallService.getAllEntries();

        //then
        StepVerifier.create(allEntries)
                .assertNext(firstEntry -> assertThat(firstEntry.getName()).isEqualTo("Rekursion in Java"))
                .assertNext(secondEntry -> assertThat(secondEntry.getName()).isEqualTo("Reactive Testing"))
                .verifyComplete();
    }

    @Test
    public void getAllEntriesInvalidOrder() throws MalformedURLException {
        //given
        Flux<Entry> entryFlux = Flux.just(entry, entry2);
        when(entryRepository.findAll()).thenReturn(entryFlux);

        //when
        Flux<Entry> allEntries = rememberbrallService.getAllEntries();

        //then
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> StepVerifier.create(allEntries)
                        .expectNext(entry2, entry)
                        .verifyComplete()).withMessageContaining("expectation \"expectNext(Entry(id=");
    }

    @Test
    public void testFirstOfAllEntries() throws MalformedURLException {
        //given
        Flux<Entry> entryFlux = Flux.just(entry, entry2);
        when(entryRepository.findAll()).thenReturn(entryFlux);

        //when
        Flux<Entry> allEntries = rememberbrallService.getAllEntries();

        //then
        StepVerifier.create(allEntries)
                .assertNext(entry -> {
                    assertThat(entry.getId()).isInstanceOf(String.class);
                    assertThat(entry.getName()).isInstanceOf(String.class);
                    assertThat(entry.getCategory()).isInstanceOf(EntryCategory.class);
                    assertThat(entry.getUrl()).isInstanceOf(URL.class);
                })
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void getEntryByID() {
        //given
        when(entryRepository.findById("00000000-0000-0000-0000-000000000001")).thenReturn(Mono.just(entry));

        //when
        Mono<Entry> existingEntry = rememberbrallService.getEntryByID("00000000-0000-0000-0000-000000000001");

        //then
        StepVerifier
                .create(existingEntry)
                .expectNextMatches(e -> e.equals(this.entry))
                .verifyComplete();
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
