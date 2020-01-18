package deduplicator.controller;

import deduplicator.model.Element;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DataController {

    @RequestMapping(path="/data", method=RequestMethod.GET)
    public List<Element> getData(){
        // TODO replace with real data
        // Test Data
        Element element = new Element();
        element.name = "test";
        element.lastName = "1";
        element.lastName = "1";
        element.email = "1";
        element.phone = "1";
        element.active = "1";

        List<Element> elements = new ArrayList<>();
        elements.add(element);
        elements.add(element);
        elements.add(element);
        elements.add(element);
        elements.add(element);
        elements.add(element);
        elements.add(element);
        return elements;
    }
}