package deduplicator.service;

import deduplicator.model.Duplicate;
import deduplicator.model.DuplicateDTO;
import deduplicator.model.DuplicateDetails;
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
import static java.util.stream.Collectors.*;

// TODO write unit tests
@Service
public class DeduplicatorService {
    private static final String ROOT = ""; // TODO replace with parameter
    private static Logger LOGGER = LoggerFactory.getLogger(DeduplicatorService.class);

    @Autowired
    private DeduplicatorRepository repository;

    public List<Duplicate> getDuplicateFiles() {
        try {
            try (Stream<Path> paths = Files.walk(get(ROOT), MAX_VALUE)) {
                List<Duplicate> groupBySize = traverseFileTree(paths);

                List<Duplicate> groupByStartBytes = groupByStartBytes(groupBySize);

                List<Duplicate> groupByChecksum = groupByCheckSum(groupByStartBytes);

                persistDuplicates(groupByChecksum);

                return groupByChecksum;
            }
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return new ArrayList<>();
    }

    // TODO needs rework
    private void persistDuplicates(List<Duplicate> duplicateList) {
        for (Duplicate duplicate : duplicateList) {
            for (DuplicateDetails details : duplicate.paths) {
                DuplicateDTO duplicateFromDb = repository.getDuplicateBySizeAndStartBytesAndPath(duplicate.size, duplicate.startBytes, details.location.toString());
                if (duplicateFromDb != null) {
                    if (duplicateFromDb.lastModified.equals(details.lastModified)) {
                        duplicateFromDb.checkSum = duplicate.checkSum;
                        repository.save(duplicateFromDb);
                    } else {
                        repository.delete(duplicateFromDb);
                        createDuplicateDTO(duplicate, details);
                    }
                } else {
                    createDuplicateDTO(duplicate, details);
                }
            }
        }

        repository.flush();
    }

    private void createDuplicateDTO(Duplicate duplicate, DuplicateDetails details) {
        DuplicateDTO duplicateDTO = new DuplicateDTO();
        duplicateDTO.checkSum = duplicate.checkSum;
        duplicateDTO.name = duplicate.name;
        duplicateDTO.size = duplicate.size;
        duplicateDTO.path = details.location.toString();
        duplicateDTO.startBytes = duplicate.startBytes;
        duplicateDTO.lastModified = details.lastModified;
        repository.save(duplicateDTO);
    }

    private Map<String, String> getValueAsKey(Set<Map.Entry<String, List<DuplicateDetails>>> values) {
        return values.stream().filter(v -> v.getValue().size() > 1).collect(toMap(k -> k.getValue().get(0).location.toString(), Map.Entry::getKey));
    }

    private List<Duplicate> createDuplicates(Set<Map.Entry<String, List<Path>>> values) {
        return values.stream().filter(e -> e.getValue().size() > 1).map(Duplicate::new).collect(toList());
    }

    private List<Duplicate> getStartingBytes(Set<Map.Entry<String, List<DuplicateDetails>>> values, List<Duplicate> duplicates) {
        Map<String, String> startBytes = getValueAsKey(values);

        return duplicates.stream().filter(duplicate -> startBytes.containsKey(duplicate.name)).map(duplicate -> {
            duplicate.startBytes = startBytes.get(duplicate.name);
            return duplicate;
        }).collect(toList());
    }

    private List<Duplicate> traverseFileTree(Stream<Path> paths) {
        Map<String, List<Path>> map = paths.filter(Files::isRegularFile).collect(groupingBy(this::getFileSize));
        return createDuplicates(map.entrySet());
    }

    private List<Duplicate> groupByStartBytes(List<Duplicate> groupBySize) {
        Map<String, List<DuplicateDetails>> collect = flatten(groupBySize).collect(groupingBy(this::readStartingBytes));
        return getStartingBytes(collect.entrySet(), groupBySize);
    }

    private List<Duplicate> groupByCheckSum(List<Duplicate> entries) {
        List<DuplicateDTO> duplicatesWithCheckSum = entries.stream().flatMap(d -> repository.getDuplicatesBySizeAndStartBytes(d.size, d.startBytes).stream()).collect(toList());

        // TODO needs rework
        return entries.stream().map(d -> {
            if (d.checkSum == null || d.checkSum.isEmpty()) {
                Map<String, List<DuplicateDetails>> collect = d.paths.stream().collect(groupingBy(file -> MD5CheckSum.getFileChecksum(file.location, d, duplicatesWithCheckSum)));
                for (Map.Entry<String, List<DuplicateDetails>> c : collect.entrySet()) {
                    if (c.getValue().size() > 1) {
                        d.checkSum = c.getKey();
                        d.paths = c.getValue();
                    }
                }
            }
            return d;
        }).collect(toList());
    }

    private Stream<DuplicateDetails> flatten(List<Duplicate> value) {
        return value.stream().filter(duplicate -> duplicate.paths.size() > 1).flatMap(duplicate -> duplicate.paths.stream());
    }

    private String readStartingBytes(DuplicateDetails value) {
        byte[] buffer = new byte[1024];
        try (InputStream is = new FileInputStream(value.location.toString())) {
            is.read(buffer);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return Arrays.toString(buffer);
    }

    private String getFileSize(Path path) {
        try {
            return String.valueOf(Files.size(path));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return "0";
    }
}
