package de.rememberbrall;

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

    public Mono<Entry> getEntryByUUID(String entryId) {
        return entryRepository.findById(entryId);
    }

    public Mono<Entry> createEntry(Entry newEntry) {
        return entryRepository.insert(newEntry);
    }

    public Mono<Void> deleteEntry(String entryId) {
        return entryRepository.deleteById(entryId);
    }

}
