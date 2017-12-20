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
@JsonIgnoreProperties(value = "id", allowGetters = true)
@Document
public class Entry {
    @Id
    private String id;
    @Size(min = 2, max = 30, message = "You must use at least 2 and no more than 30 characters")
    private String name;
    private EntryCategory category;
    private URL url;

    public Entry(String name, EntryCategory category, URL url) {
        this.name = name;
        this.category = category;
        this.url = url;
    }
}
