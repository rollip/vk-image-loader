package ru.helper.vkimageloader.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.client.wrapper.VkApiClientWrapper;
import ru.helper.vkimageloader.client.wrapper.VkAuthClientWrapper;
import ru.helper.vkimageloader.dto.business.PhotoToDownload;
import ru.helper.vkimageloader.dto.vk.HistoryAttachmentsResponse;
import ru.helper.vkimageloader.facade.VkLoaderFacade;
import ru.helper.vkimageloader.helper.DownloadSpeedCounter;
import ru.helper.vkimageloader.helper.PhotoDownloadHelper;
import ru.helper.vkimageloader.helper.PhotoSaverHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class VkLoaderFacadeImpl implements VkLoaderFacade {

    private final PhotoDownloadHelper photoDownloadHelper;
    private final PhotoSaverHelper fileSaverHelper;
    private final ExecutorService photoDownloadExecutor;
    private final VkApiClientWrapper vkApiClientWrapper;
    private final VkAuthClientWrapper vkAuthClientWrapper;
    private final DownloadSpeedCounter downloadSpeedCounter;

    public void loadPhotos() {

        String startFrom = "0";
        List<PhotoToDownload> photoToDownloads = new ArrayList<>();
        while (Objects.nonNull(startFrom)) {
            HistoryAttachmentsResponse responseDto = vkApiClientWrapper.getHistoryAttachments(startFrom);

            if (Objects.isNull(responseDto.response())) {
                vkAuthClientWrapper.refreshToken();
                continue;
            }

            startFrom = responseDto.response().nextFrom();

            List<PhotoToDownload> batch = responseDto.response().items().stream()
                    .map(HistoryAttachmentsResponse.Item::attachment)
                    .map(HistoryAttachmentsResponse.Attachment::photo)
                    .filter(Objects::nonNull)
                    .map(this::toPhotoToDownload)
                    .toList();

            photoToDownloads.addAll(batch);
        }

        downloadSpeedCounter.setTotalFiles(photoToDownloads.size());

        processAll(photoToDownloads);
    }

    private void processAll(List<PhotoToDownload> photos) {
        int batchSize = 200;
        for (int i = 0; i < photos.size(); i += batchSize) {
            int end = Math.min(i + batchSize, photos.size());
            List<PhotoToDownload> batch = photos.subList(i, end);
            processBatch(batch);
        }
    }

    private HistoryAttachmentsResponse.PhotoSize findBestQuality(List<HistoryAttachmentsResponse.PhotoSize> list) {
        return list.stream().max(Comparator.comparingInt(size -> size.height() * size.width())).orElse(null);
    }

    private PhotoToDownload toPhotoToDownload(HistoryAttachmentsResponse.Photo photo) {
        HistoryAttachmentsResponse.PhotoSize bestSize = photo.origPhoto() != null ? photo.origPhoto() : findBestQuality(photo.sizes());
        return new PhotoToDownload(photo.id(), photo.date(), bestSize.url());
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

            byte[] bytes = photoDownloadHelper.download(photo.url());
            fileSaverHelper.saveFile(bytes, photo.id() + ".jpg", photo.date());
            downloadSpeedCounter.increment();

        } catch (Exception e) {
            log.error("Failed to download photo {}: {}", photo.id(), e.getMessage());
        }
    }
}
