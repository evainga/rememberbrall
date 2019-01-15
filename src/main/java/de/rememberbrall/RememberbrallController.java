package de.rememberbrall;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class RememberbrallController {

    private RememberbrallService rememberbrallService;

    @TrackTime
    @GetMapping(path = "/entries", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_STREAM_JSON_VALUE })
    public Flux<Entry> showAllEntries() {
        return rememberbrallService.getAllEntries();
    }


    @PostMapping("/entries")
    public Mono<ResponseEntity<Entry>> createEntry(@Valid @RequestBody Entry entry) {

        return rememberbrallService.createEntry(entry)
                .map(Entry::getId)
                .flatMap(id -> {
                    try {
                        return Mono.just(ResponseEntity.created(new URI(id)).build());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return Mono.empty();
                });
    }

    @TrackTime
    @GetMapping(path = "/entries/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<ResponseEntity<Entry>> showSpecificEntry(@PathVariable String id) {
        return rememberbrallService.getEntryByID(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/entries")
    public Mono<ResponseEntity<Void>> deleteAllEntries() {
        return rememberbrallService.deleteAllEntries()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/entries/{id}")
    public Mono<ResponseEntity<Void>> deleteEntry(@PathVariable String id) {
        return rememberbrallService.deleteEntry(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @PutMapping(path = "/entries/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<ResponseEntity<Entry>> updateEntry(@PathVariable String id,
            @RequestBody Entry entry) {

        return rememberbrallService.updateEntry(id, entry)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
