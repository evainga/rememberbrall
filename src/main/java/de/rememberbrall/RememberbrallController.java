package de.rememberbrall;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RememberbrallController {

    @Autowired
    private RememberbrallService rememberbrallService;
    @Autowired
    private EntryRepository entryRepository;

    @GetMapping(path = "/entries")
    public Flux<Entry> showAllEntries() {
        return rememberbrallService.getAllEntries();
    }

    @GetMapping(path = "/entries/{entryId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Entry> showSpecificEntry(@PathVariable String entryId) {
        Mono<Entry> mono = rememberbrallService.getEntryByUUID(entryId);

        if (mono.hasElement().block()) {
            return ResponseEntity.ok(mono.block());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/entries")
    public ResponseEntity<Entry> createEntry(@Valid @RequestBody Entry entry) {
        String entryId = rememberbrallService.createEntry(entry).block().getEntryId();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, entryId);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/entries/{entryId}")
    public ResponseEntity<?> deleteEntry(@PathVariable String entryId) {
        rememberbrallService.deleteEntry(entryId);
        return ResponseEntity.noContent().build();
    }
}
