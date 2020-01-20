package deduplicator.service;

import deduplicator.model.Element;
import deduplicator.util.MD5CheckSum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

// TODO write unit tests
public class Deduplicator {
    private static final String ROOT = ""; // TODO replace with parameter
    private static Logger LOGGER = LoggerFactory.getLogger(Deduplicator.class);

    public static List<Element> getDuplicates() {
        try {
            try (Stream<Path> paths = Files.walk(get(ROOT), MAX_VALUE)) {

                Map<Long, List<Path>> groupBySize = traverseFileTree(paths);

                Map<String, List<Path>> groupByStartBytes = groupByStartBytes(groupBySize);

                Map<String, List<Path>> groupByChecksum = groupByCheckSum(groupByStartBytes);

                List<Element> result = getResultAsElement(groupByChecksum);
//                result.entrySet().forEach(System.out::println);
                return result;
            }
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return new ArrayList<>();
    }

    private static List<Element> getResultAsElement(Map<String, List<Path>> groupByChecksum) {
        return groupByChecksum.values()
                              .stream()
                              .filter(f -> f.size() > 1)
                              .map(Element::new)
                              .collect(toList());
    }

    private static Map<String, List<Path>> groupByCheckSum(Map<String, List<Path>> groupByStartBytes) {
        List<Path> groupByStartBytesFilter = groupByStartBytes.values()
                                                              .stream()
                                                              .filter(f -> f.size() > 1)
                                                              .flatMap(List<Path>::stream)
                                                              .collect(Collectors.toList());
        return groupByStartBytesFilter.stream()
                                      .collect(groupingBy(MD5CheckSum::getFileChecksum));
    }

    private static Map<String, List<Path>> groupByStartBytes(Map<Long, List<Path>> groupBySize) {
        List<Path> groupBySizeFilter = groupBySize.values()
                                                  .stream()
                                                  .filter(entry -> entry.size() > 1)
                                                  .flatMap(List<Path>::stream)
                                                  .collect(toList());
        return groupBySizeFilter.stream()
                                .collect(groupingBy(Deduplicator::getStartBytes));
    }

    private static Map<Long, List<Path>> traverseFileTree(Stream<Path> paths) {
        List<Path> pathsList = paths.filter(Files::isRegularFile)
                                    .collect(toList());
        return pathsList.stream()
                        .collect(groupingBy(Deduplicator::getFileSize));
    }

    private static String getEndBytes(Path value) {
        return "";
    }

    private static String getStartBytes(Path value) {
        byte[] buffer = new byte[1000];
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
