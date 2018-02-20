package de.rememberbrall;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RememberbrallService {

    @Autowired
    private EntryRepository entryRepository;

    public Flux<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    public Mono<Entry> getEntryByID(String id) {
        return entryRepository.findById(id);
    }

    public Mono<Entry> createEntry(Entry newEntry) {
        return entryRepository.save(newEntry);
    }

    public Mono<Void> deleteEntry(String id) {
        return entryRepository.deleteById(id);
    }

    public Mono<Void> deleteAllEntries() {
        return entryRepository.deleteAll();
    }

    public Mono<Entry> changeEntry(String id, String newName, URL newUrl, EntryCategory newEntryCategory) {
        Entry changeEntry = entryRepository.findById(id).block();

        if (newName != null) {
            changeEntry.setName(newName);
        }
        if (newUrl != null) {
            changeEntry.setUrl(newUrl);
        }
        if (newEntryCategory != null) {
            changeEntry.setCategory(newEntryCategory);
        }

        return entryRepository.save(changeEntry);
    }

}
