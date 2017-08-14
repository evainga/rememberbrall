package de.rememberbrall;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

public class RememberbrallController {

    @Autowired
    private RememberbrallService rememberbrallService;

    @GetMapping(path = "/entrys", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Entry> showAllEntrys() {
        return rememberbrallService.getAllEntrys();
    }

}
