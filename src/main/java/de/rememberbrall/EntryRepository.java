package de.rememberbrall;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EntryRepository extends ReactiveMongoRepository<Entry, String> {
}