package deduplicator.model;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Duplicate implements Serializable {

    public String size;

    public String name;

    public List<Path> paths;

    public String startBytes;

    public String checkSum;

    public Duplicate(Map.Entry<String, List<Path>> path) {
        this.size = path.getKey();
        this.paths = path.getValue();
        this.name = path.getValue().get(0).toString();
    }

}
