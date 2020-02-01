package deduplicator.model;

import java.nio.file.Path;

public class Duplicate {
    public String path;

    public Duplicate(Path path) {
        this.path = path.toString();
    }
}
