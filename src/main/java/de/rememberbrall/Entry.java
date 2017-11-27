package de.rememberbrall;

import java.net.URL;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = "entryId", allowGetters = true)
@Document
public class Entry {

    @Id
    private String entryId;
    @Size(min = 2, max = 30, message = "You must use at least 2 and no more than 30 characters")
    private String entryName;
    private EntryCategory entryCategory;
    private URL entryUrl;

    public Entry(String entryName, EntryCategory entryCategory, URL entryUrl) {
        this.entryName = entryName;
        this.entryCategory = entryCategory;
        this.entryUrl = entryUrl;
    }
}
