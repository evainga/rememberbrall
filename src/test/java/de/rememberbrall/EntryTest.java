package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.junit.Test;

public class EntryTest {
    @Test
    public void allArgsConstructor() throws MalformedURLException {
        Entry testEntry = new Entry("4414177a-8b5b-4e1f-8fe8-eb736f39ce13", "LINUX",
                EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));
        testEntry.setEntryId("4414177a-8b5b-4e1f-8fe8-eb736f39ce13");
        assertThat(testEntry.getEntryId()).isEqualTo(UUID.fromString("4414177a-8b5b-4e1f-8fe8-eb736f39ce13"));
        assertThat(testEntry.getEntryCategory()).isEqualTo(EntryCategory.LINUX);
        assertThat(testEntry.getEntryName()).isEqualTo("LINUX");
        assertThat(testEntry.getEntryUrl()).isEqualTo(new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));
    }

    @Test
    public void requiredArgsConstructor() throws MalformedURLException {
        Entry testEntry = new Entry("Linux", EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/(9885)_Linux"));
        assertThat(testEntry.getEntryId(), is(nullValue()));
        assertThat(testEntry.getEntryCategory()).isEqualTo(EntryCategory.LINUX);
        assertThat(testEntry.getEntryName()).isEqualTo("Linux");
        assertThat(testEntry.getEntryUrl()).isEqualTo(new URL("https://de.wikipedia.org/wiki/(9885)_Linux"));

    }

}
