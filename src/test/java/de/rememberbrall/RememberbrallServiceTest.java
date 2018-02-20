package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class RememberbrallServiceTest extends MockitoTest {

    private static final String ENTRY_ID = "00000000-0000-0000-0000-000000000001";
    private static final String ENTRY_ID_2 = "00000000-0000-0000-0000-000000000002";

    private Entry entry;
    private Entry entry2;

    @InjectMocks
    private RememberbrallService rememberbrallService;

    @Mock
    private EntryRepository entryRepository;


    @BeforeTest
    public void createInitialEntries() throws MalformedURLException {
        entry = new Entry(ENTRY_ID, "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
        entry2 = new Entry(ENTRY_ID_2, "Reactive Testing", EntryCategory.ENTWICKLUNG,
                new URL("http://projectreactor.io/docs/core/release/reference/docs/index.html#testing"));
    }


    @Test
    public void getAllEntriesValidOrder() {
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
    public void getAllEntriesInvalidOrder() {
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
    public void testFirstOfAllEntries() {
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
        when(entryRepository.findById(ENTRY_ID)).thenReturn(Mono.just(entry));


        //when
        Mono<Entry> existingEntry = rememberbrallService.getEntryByID(ENTRY_ID);

        //then
        StepVerifier
                .create(existingEntry)
                .expectNextMatches(e -> e.equals(entry))
                .verifyComplete();
    }

    @Test
    public void getNonExistingEntry() {
        //given
        when(entryRepository.findById(ENTRY_ID)).thenReturn(Mono.empty());


        //when
        Mono<Entry> nonExistingEntry = rememberbrallService.getEntryByID(ENTRY_ID);

        //then
        StepVerifier
                .create(nonExistingEntry)
                .verifyComplete();
    }

    @Test
    public void createEntry() {
        //given
        Mono<Entry> newMonoEntry = Mono.just(entry);
        when(entryRepository.save(entry)).thenReturn(newMonoEntry);

        //when
        Mono<Entry> newEntry = rememberbrallService.createEntry(entry);

        //then
        assertThat(newEntry).isNotNull();
    }

    @Test
    public void deleteEntry() {
        //given
        when(entryRepository.deleteById(ENTRY_ID)).thenReturn(Mono.empty());

        //when
        Mono<Void> deleteEntry = rememberbrallService.deleteEntry(ENTRY_ID);

        //then
        StepVerifier
                .create(deleteEntry)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void deleteAllEntries() {
        //given
        when(entryRepository.deleteAll()).thenReturn(Mono.empty());

        //when
        Mono<Void> deleteEntry = rememberbrallService.deleteAllEntries();

        //then
        StepVerifier
                .create(deleteEntry)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void updateEntryCompletely() throws MalformedURLException {
        //given
        Entry entry = new Entry(ENTRY_ID, "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
        when(entryRepository.findById(ENTRY_ID)).thenReturn(Mono.just(entry));

        //when
        rememberbrallService.updateEntry(ENTRY_ID, new Entry("New Entry Name", EntryCategory.LINUX, new URL("http://www.new-url.de")));

        //then
        assertThat(entry.getName()).isEqualTo("New Entry Name");
        assertThat(entry.getUrl()).isEqualTo(new URL("http://www.new-url.de"));
        assertThat(entry.getCategory()).isEqualTo(EntryCategory.LINUX);
    }

    @Test
    public void updateNonExistingEntry() throws MalformedURLException {
        //given
        when(entryRepository.findById(ENTRY_ID)).thenReturn(Mono.empty());

        //when
        Mono<Entry> nonExistingEntry = rememberbrallService.updateEntry(ENTRY_ID, new Entry("New Entry Name", EntryCategory.JAVA,
                new URL("http://non-existing.com")));

        //then
        StepVerifier
                .create(nonExistingEntry)
                .verifyComplete();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void updateEntryWithoutId() throws MalformedURLException {
        rememberbrallService.updateEntry(null, new Entry("New Entry Name", EntryCategory.JAVA,
                new URL("http://non-existing.com")));
    }

}
