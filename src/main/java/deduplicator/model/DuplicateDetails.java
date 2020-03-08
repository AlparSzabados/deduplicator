package deduplicator.model;

import java.nio.file.Path;

public class DuplicateDetails {

    public Path location;
    public String size;
    public String lastModified;

    public DuplicateDetails(Path location, String size, String lastModified) {
        this.location = location;
        this.size = size;
        this.lastModified = lastModified;
    }
}
