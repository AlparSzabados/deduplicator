package deduplicator.model;

import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Element {
    public String name;
    public List<Duplicate> locations;

    public Element(List<Path> duplicates) {
        this.name = duplicates.get(0).toString();
        this.locations = duplicates.stream().map(path -> new Duplicate(path)).collect(toList());
    }

}
