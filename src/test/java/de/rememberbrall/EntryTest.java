package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class EntryTest {
    @Test
    public void allArgsConstructor() throws MalformedURLException {
        Entry testEntry = new Entry("4414177a-8b5b-4e1f-8fe8-eb736f39ce13", "LINUX",
                EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));
        testEntry.setId("4414177a-8b5b-4e1f-8fe8-eb736f39ce13");
        assertThat(testEntry.getId()).isEqualTo("4414177a-8b5b-4e1f-8fe8-eb736f39ce13");
        assertThat(testEntry.getCategory()).isEqualTo(EntryCategory.LINUX);
        assertThat(testEntry.getName()).isEqualTo("LINUX");
        assertThat(testEntry.getUrl()).isEqualTo(new URL("https://de.wikipedia.org/wiki/Linux_(Waschmittel)"));
    }

    @Test
    public void requiredArgsConstructor() throws MalformedURLException {
        Entry testEntry = new Entry("Linux", EntryCategory.LINUX, new URL("https://de.wikipedia.org/wiki/(9885)_Linux"));
        assertThat(testEntry.getId(), is(nullValue()));
        assertThat(testEntry.getCategory()).isEqualTo(EntryCategory.LINUX);
        assertThat(testEntry.getName()).isEqualTo("Linux");
        assertThat(testEntry.getUrl()).isEqualTo(new URL("https://de.wikipedia.org/wiki/(9885)_Linux"));

    }

}
