package de.rememberbrall;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;

@Service
public class RememberbrallService {
    private List<Entry> entryDb = new ArrayList<>();

    @PostConstruct
    @VisibleForTesting
    void setupDb() throws MalformedURLException {
        Entry rekursion = new Entry(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Rekursion in Java", EntryCategory.JAVA,
                new URL("http://www.java-programmieren.com/rekursion-in-java.php"));
        Entry betterJava = new Entry(UUID.fromString("00000000-0000-0000-0000-000000000002"), "4 Techniques for Writing better Java", EntryCategory.JAVA,
                new URL("https://dzone.com/articles/4-techniques-for-writing-better-java"));
        Entry goldenRule = new Entry(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Goldern Rule of Rebasing", EntryCategory.GIT,
                new URL("https://www.atlassian.com/git/tutorials/merging-vs-rebasing#the-golden-rule-of-rebasing"));
        Entry mocksNotStubs = new Entry(UUID.fromString("00000000-0000-0000-0000-000000000004"), "Mocks aren't Stubs", EntryCategory.ENTWICKLUNG,
                new URL("https://martinfowler.com/articles/mocksArentStubs.html"));
        entryDb.add(rekursion);
        entryDb.add(betterJava);
        entryDb.add(goldenRule);
        entryDb.add(mocksNotStubs);
    }

    public List<Entry> getAllEntries() {
        return entryDb;

    }

    public Optional<Entry> getEntryByUUID(UUID uuid) {
        return entryDb.stream().filter(entry -> entry.getEntryId().equals(uuid)).findFirst();
    }

    public UUID createEntry(Entry newEntry) {
        UUID uuid = UUID.randomUUID();
        newEntry.setEntryId(uuid);
        entryDb.add(newEntry);
        return uuid;
    }

    public boolean deleteEntry(UUID uuid) {
        Optional<Entry> deleteEntry = getEntryByUUID(uuid);
        if (!deleteEntry.isPresent())
            return false;
        return entryDb.remove(deleteEntry.get());
    }

}
