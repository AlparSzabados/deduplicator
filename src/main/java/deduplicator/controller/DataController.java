package deduplicator.controller;

import deduplicator.model.Element;
import deduplicator.service.Deduplicator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class DataController {

    @GetMapping("/data")
    public List<Element> getData() {
        return Deduplicator.getDuplicates();
    }
}