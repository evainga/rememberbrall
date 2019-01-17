package de.rememberbrall;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RememberbrallService {

    private final EntryRepository entryRepository;

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

    public Mono<Entry> updateEntry(String id, Entry updatedEntry) {
        Mono<Entry> existingEntry = entryRepository.findById(id);
        if (existingEntry.equals(Mono.empty())) {
            return existingEntry;
        } else {
            return existingEntry
                    .flatMap(entry -> {
                        entry.setName(updatedEntry.getName());
                        entry.setCategory(updatedEntry.getCategory());
                        entry.setUrl(updatedEntry.getUrl());
                        return entryRepository.save(entry);
                    });
        }
    }
}
