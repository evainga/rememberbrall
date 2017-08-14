package de.rememberbrall;

import java.net.URL;
import java.util.UUID;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties(value = "entryId", allowGetters = true)
public class Entry {

    private UUID entryId;
    @Size(min = 2, max = 30, message = "You must use at least 2 and no more than 30 characters")
    private final String entryName;
    private final EntryCategory entryCategory;
    private final URL entryUrl;
}
