package de.rememberbrall;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class RememberbrallController {

    @Autowired
    private RememberbrallService rememberbrallService;

    //    @GetMapping(path = "/")
    //    public ModelAndView forwardToRestDocumentation() {
    //        return new ModelAndView("forward:/docs/index.html");
    //    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/events")
    public String events(final Model model) {

        final Flux<Entry> playlistStream = this.rememberbrallService.getAllEntries();

        final IReactiveDataDriverContextVariable dataDriver = new ReactiveDataDriverContextVariable(playlistStream, 1, 1);

        model.addAttribute("data", dataDriver);

        return "events";

    }

    @RequestMapping(path = "/entries", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Entry> showAllEntries(final Model model) {
        //        model.addAttribute("entries", new ReactiveDataDriverContextVariable(rememberbrallService.getAllEntries(), 10));
        //        IReactiveDataDriverContextVariable dataDriver = new ReactiveDataDriverContextVariable(rememberbrallService.getAllEntries(), 1, 1);
        //        model.addAttribute("entries", dataDriver);
        //        return "entryPage";
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

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable String id) {
        rememberbrallService.deleteEntry(id).block();
        return ResponseEntity.noContent().build();
    }
}
