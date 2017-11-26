package de.rememberbrall;

import java.net.URL;
import java.util.UUID;

import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties(value = "entryId", allowGetters = true)
@Document
public class Entry {

    private UUID entryId;
    @Size(min = 2, max = 30, message = "You must use at least 2 and no more than 30 characters")
    private final String entryName;
    private final EntryCategory entryCategory;
    private final URL entryUrl;

    public interface EntryRespository extends ReactiveMongoRepository<Entry, UUID> {
        Flux<Entry> findByUUID(UUID uuid);
    }
}
