package deduplicator.controller;

import deduplicator.model.Duplicate;
import deduplicator.repository.DeduplicatorRepository;
import org.openjdk.jmh.util.Deduplicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }
}
