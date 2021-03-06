package deduplicator.controller;

import deduplicator.model.Duplicate;
import deduplicator.service.DeduplicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class DataController {

    @Autowired
    private DeduplicatorService service;

    @GetMapping("/data")
    public List<Duplicate> getData() {
        return service.getDuplicateFiles();
    }
}