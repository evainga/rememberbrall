package de.rememberbrall;

import java.net.URL;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RememberbrallController {

    @Autowired
    private RememberbrallService rememberbrallService;

    @GetMapping(path = "/entries", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_STREAM_JSON_VALUE })
    public Flux<Entry> showAllEntries() {
        return rememberbrallService.getAllEntries();
    }

    @GetMapping(path = "/entries/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Entry> showSpecificEntry(@PathVariable String id) {
        Mono<Entry> mono = rememberbrallService.getEntryByID(id);

        if (mono.hasElement().block()) {
            return ResponseEntity.ok(mono.block());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/entries")
    public ResponseEntity<Entry> createEntry(@Valid @RequestBody Entry entry) {
        String id = rememberbrallService.createEntry(entry).block().getId();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, id);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/entries")
    public ResponseEntity<?> deleteAllEntries() {
        rememberbrallService.deleteAllEntries().block();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable String id) {
        rememberbrallService.deleteEntry(id).block();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/entries/{id}")
    public ResponseEntity<Entry> updateEntry(
            @PathVariable String id,
            @RequestParam String name,
            @RequestParam URL url,
            @RequestParam EntryCategory category) {

        Mono<Entry> updateEntry = rememberbrallService.updateEntry(id, name, url, category);

        if (updateEntry.hasElement().block()) {
            return ResponseEntity.ok(updateEntry.block());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
