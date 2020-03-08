package deduplicator.util;

import deduplicator.model.Duplicate;
import deduplicator.model.DuplicateDTO;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MD5CheckSum {
    public static String getFileChecksum(Path file, Duplicate d, List<DuplicateDTO> dtos) {
        for (DuplicateDTO dto : dtos) {
            if (dto != null && d.size.equals(dto.size) && d.startBytes.equals(dto.startBytes) && file.toString().equals(dto.path) && (dto.checkSum != null || !dto.checkSum.isEmpty()))
                return dto.checkSum;
        }

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
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            result = sb.toString();

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // return complete hash
        return result;
    }

}
