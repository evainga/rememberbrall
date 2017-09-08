package de.rememberbrall;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

@RestController
public class RememberbrallController {

    @Autowired
    private RememberbrallService rememberbrallService;

    @GetMapping(path = "/entries")
    public List<Entry> showAllEntries() {
        return rememberbrallService.getAllEntries();
    }

    @GetMapping(path = "/entries/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Entry> showSpecificEntry(@PathVariable UUID uuid) {
        Optional<Entry> entry = rememberbrallService.getEntryByUUID(uuid);

        if (entry.isPresent()) {
            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/entries")
    public ResponseEntity<Entry> createEntry(@Valid @RequestBody Entry entry) {
        UUID uuid = rememberbrallService.createEntry(entry);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, uuid.toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/entries/{uuid}")
    public ResponseEntity<?> deleteEntry(@PathVariable UUID uuid) {
        if (rememberbrallService.deleteEntry(uuid)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
