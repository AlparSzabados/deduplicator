package deduplicator.service;

import deduplicator.model.Element;
import deduplicator.repository.DeduplicatorRepository;
import deduplicator.util.MD5CheckSum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

// TODO write unit tests
@Service
public class DeduplicatorService {
    private static final String ROOT = ""; // TODO replace with parameter
    private static Logger LOGGER = LoggerFactory.getLogger(DeduplicatorService.class);

    @Autowired
    private DeduplicatorRepository repository;

    public static List<Element> getDuplicates() {
        try {
            try (Stream<Path> paths = Files.walk(get(ROOT), MAX_VALUE)) {
                Map<Long, List<Path>> groupBySize = traverseFileTree(paths);
                Map<String, List<Path>> groupByStartBytes = groupByStartBytes(groupBySize);
                Map<String, List<Path>> groupByChecksum = groupByCheckSum(groupByStartBytes);
                List<Element> result = getResultAsElement(groupByChecksum);

                return result;
            }
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return new ArrayList<>();
    }

    private static Map<Long, List<Path>> traverseFileTree(Stream<Path> paths) {
        return paths.filter(Files::isRegularFile).collect(groupingBy(DeduplicatorService::getFileSize));
    }

    private static Map<String, List<Path>> groupByStartBytes(Map<Long, List<Path>> groupBySize) {
        return getDuplicates(groupBySize.values()).collect(groupingBy(DeduplicatorService::getStartBytes));
    }

    private static Map<String, List<Path>> groupByCheckSum(Map<String, List<Path>> groupByStartBytes) {
        return getDuplicates(groupByStartBytes.values()).collect(groupingBy(MD5CheckSum::getFileChecksum));
    }

    private static List<Element> getResultAsElement(Map<String, List<Path>> groupByChecksum) {
        return groupByChecksum.values()
                .stream()
                .filter(f -> f.size() > 1)
                .map(Element::new)
                .collect(toList());
    }

    private static Stream<Path> getDuplicates(Collection<List<Path>> groupBySize) {
        return groupBySize.stream()
                .filter(entry -> entry.size() > 1)
                .flatMap(List<Path>::stream);
    }

    private static String getEndBytes(Path value) {
        return "";
    }

    private static String getStartBytes(Path value) {
        byte[] buffer = new byte[1024];
        try (InputStream is = new FileInputStream(value.toString())) {
            is.read(buffer);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return Arrays.toString(buffer);
    }

    private static Long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return 0L;
    }
}
