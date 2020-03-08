package deduplicator.model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class Duplicate implements Serializable {

    public String size;

    public String name;

    public List<DuplicateDetails> paths;

    public String startBytes;

    public String checkSum;

    public Duplicate(Map.Entry<String, List<Path>> path) {
        this.size = path.getKey();
        setDetails(path.getValue());
        this.name = path.getValue().get(0).toString();
    }

    public void setDetails(List<Path> path) {
        paths = path.stream().map(p -> new DuplicateDetails(p, size, getLastModifiedDate(p))).collect(toList());
    }

    private String getLastModifiedDate(Path path) {
        FileTime fileTime;
        try {
            fileTime = Files.getLastModifiedTime(path);
            return printFileTime(fileTime);
        } catch (IOException e) {
            System.err.println("Cannot get the last modified time - " + e);
        }
        return "DATE NOT FOUND";
    }

    private static String printFileTime(FileTime fileTime) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss");
        return dateFormat.format(fileTime.toMillis());
    }

}
