package deduplicator.model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.getLastModifiedTime;
import static java.util.stream.Collectors.toList;

public class Duplicate implements Serializable {
    public static final String PATTERN = "dd/MM/yyyy - hh:mm:ss";
    public String size;
    public String name;
    public List<DuplicateDetails> details;
    public String startBytes;
    public String checkSum;
    public String duplicateNumber;

    public Duplicate(Map.Entry<String, List<Path>> path) {
        this.size = path.getKey();
        setDetails(path.getValue());
        this.name = path.getValue().get(0).toString();
        duplicateNumber = String.valueOf(details.size());
    }

    public void setDetails(List<Path> path) {
        details = path.stream().map(this::createDetails).collect(toList());
    }

    private String getLastModifiedDate(Path path) {
        FileTime fileTime;
        try {
            fileTime = getLastModifiedTime(path);
            return formatTimestamp(fileTime);
        } catch (IOException e) {
//            log.error("Cannot get the last modified time - " + e);
        }
        return "DATE NOT FOUND";
    }

    private static String formatTimestamp(FileTime fileTime) {
        final DateFormat dateFormat = new SimpleDateFormat(PATTERN);
        return dateFormat.format(fileTime.toMillis());
    }

    private DuplicateDetails createDetails(Path path) {
        return new DuplicateDetails(path, size, getLastModifiedDate(path));
    }
}
