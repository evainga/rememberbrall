package de.rememberbrall;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RememberbrallController {

    @Autowired
    private RememberbrallService rememberbrallService;

    @GetMapping(path = "/entries")
    public List<Entry> showAllEntries() {
        return rememberbrallService.getAllEntries();
    }

}
