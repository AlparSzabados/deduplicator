package deduplicator;

import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Deduplicator {

    private static final String ROOT = "C:/temp";

    public static void main(String... args) {
        try {
            try (Stream<Path> paths = Files.walk(get(ROOT), MAX_VALUE)) {

                List<Path> pathsList = paths.filter(Files::isRegularFile)
                        .collect(toList());

                Map<Long, List<Path>> groupBySize = pathsList.stream()
                        .collect(groupingBy(Deduplicator::getFileSize));

                List<Path> groupBySizeFilter = groupBySize.values()
                        .stream()
                        .filter(entry -> entry.size() > 1)
                        .flatMap(List<Path>::stream)
                        .collect(toList());

                Map<String, List<Path>> groupByStartBytes = groupBySizeFilter.stream()
                        .collect(groupingBy(Deduplicator::getStartBytes));

                List<Path> groupByStartBytesFilter = groupByStartBytes.values()
                        .stream()
                        .filter(f -> f.size() > 1)
                        .flatMap(List<Path>::stream)
                        .collect(Collectors.toList());

                Map<String, List<Path>> groupByChecksum = groupByStartBytesFilter.stream()
                        .collect(groupingBy(Deduplicator::getFileChecksum));

                List<List<Path>> groupByChecksumFilter = groupByChecksum.values()
                        .stream()
                        .filter(f -> f.size() > 1)
                        .collect(Collectors.toList());

                groupByChecksumFilter.stream().forEach(System.out::println);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileChecksum(Path file) {
        String result = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file.toString());

            // Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            // Read file data and update in message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }

            // close the stream; We don't need it now.
            fis.close();

            // Get the hash's bytes
            byte[] bytes = digest.digest();

            // This bytes[] has bytes in decimal format;
            // Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            result = sb.toString();

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // return complete hash
        return result;
    }

    private static String getEndBytes(Path value) {
        return "";
    }

    private static String getStartBytes(Path value) {
        byte[] buffer = new byte[1000];
        try (InputStream is = new FileInputStream(value.toString())) {
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.toString(buffer);
    }

    private static Long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
