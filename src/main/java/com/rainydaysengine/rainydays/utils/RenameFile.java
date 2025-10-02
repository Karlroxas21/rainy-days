package com.rainydaysengine.rainydays.utils;

import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class RenameFile {

    public static String rename(MultipartFile file, String user) {
        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Current time in UTC
        Instant nowInUtc = Instant.now();

        // Format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy-HH-mm-ss").withZone(ZoneId.of("UTC"));
        String formattedUtcTime = formatter.format(nowInUtc);

        // Sample file name: karl_entry_09-24-25-14-51-18.jpeg
        String newFileName = user + "_entry_" + formattedUtcTime + extension;

        return newFileName;
    }
}
