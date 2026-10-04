package ru.helper.vkimageloader.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.helper.vkimageloader.client.PhotoDownloadClient;
import ru.helper.vkimageloader.dto.business.PhotoToDownload;
import ru.helper.vkimageloader.helper.DownloadSpeedCounter;
import ru.helper.vkimageloader.helper.PhotoSaverHelper;
import ru.helper.vkimageloader.service.PhotoDownloadProcessor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoDownloadProcessorImpl implements PhotoDownloadProcessor {

    private final PhotoDownloadClient photoDownloadClient;
    private final PhotoSaverHelper fileSaverHelper;
    private final ExecutorService photoDownloadExecutor;
    private final DownloadSpeedCounter downloadSpeedCounter;

    public void processAll(List<PhotoToDownload> photos) {
        downloadSpeedCounter.setTotalFiles(photos.size());
        downloadSpeedCounter.start();
        int batchSize = 200;
        for (int i = 0; i < photos.size(); i += batchSize) {
            int end = Math.min(i + batchSize, photos.size());
            List<PhotoToDownload> batch = photos.subList(i, end);
            processBatch(batch);
        }
        downloadSpeedCounter.stop();
    }


    private void processBatch(List<PhotoToDownload> batch) {
        List<CompletableFuture<Void>> futures = batch.stream()
                .map(photo -> CompletableFuture.runAsync(
                        () -> downloadPhoto(photo), photoDownloadExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    private void downloadPhoto(PhotoToDownload photo) {
        try {
            if (fileSaverHelper.fileExists(photo.id() + ".jpg", photo.date())) {
                log.info("File exists {}", photo.id() + ".jpg");
                downloadSpeedCounter.decrementTotal();
                return;
            }

            byte[] bytes = photoDownloadClient.download(photo.url());
            fileSaverHelper.saveFile(bytes, photo.id() + ".jpg", photo.date());
            downloadSpeedCounter.increment();

        } catch (Exception e) {
            log.error("Failed to download photo {}: {}", photo.id(), e.getMessage());
        }
    }
}
