package ru.helper.vkimageloader.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
@Slf4j
public class PhotoSaverHelper {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");

    public boolean fileExists(String fileName, long unixTimestamp) {
        Path path = buildPath(unixTimestamp);
        Path filePath = path.resolve(fileName);
        return Files.exists(filePath);
    }

    public void saveFile(byte[] bytes, String fileName, long unixTimestamp) throws IOException {
        Path path = buildPath(unixTimestamp);
        Files.createDirectories(path);
        Path filePath = path.resolve(fileName);
        Files.write(filePath, bytes);
        //log.info("File {} has been saved at {}", fileName, filePath);
    }

    private Path buildPath(long unixTimestamp) {
        LocalDate date = Instant.ofEpochSecond(unixTimestamp)
                .atZone(ZONE_ID)
                .toLocalDate();

        return Path.of(
                "photos",
                String.valueOf(date.getYear()),
                "%02d".formatted(date.getMonthValue())
        );
    }
}