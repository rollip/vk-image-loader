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

    public boolean fileExists(String fileName, long unixTimestamp, Long peerId) {
        Path path = buildPath(unixTimestamp, peerId);
        Path filePath = path.resolve(fileName);
        return Files.exists(filePath);
    }

    public void saveFile(byte[] bytes, String fileName, long unixTimestamp, Long peerId) throws IOException {
        Path path = buildPath(unixTimestamp, peerId);
        Files.createDirectories(path);
        Path filePath = path.resolve(fileName);
        Files.write(filePath, bytes);
        log.debug("File {} has been saved at {}", fileName, filePath);
    }

    private Path buildPath(long unixTimestamp, Long peerId) {
        LocalDate date = Instant.ofEpochSecond(unixTimestamp)
                .atZone(ZONE_ID)
                .toLocalDate();

        return Path.of(
                "photos",
                peerId.toString(),
                String.valueOf(date.getYear()),
                "%02d".formatted(date.getMonthValue())
        );
    }
}