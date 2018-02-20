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

    public Mono<Entry> updateEntry(String id, Entry entry) {
        Mono<Entry> entryById = entryRepository.findById(id);

        if (entryById.equals(Mono.empty())) {
            return entryById;
        } else {
            Entry newEntry = entryById.block();
            newEntry.setName(entry.getName());
            newEntry.setUrl(entry.getUrl());
            newEntry.setCategory(entry.getCategory());
            return entryRepository.save(newEntry);
        }
    }

}
