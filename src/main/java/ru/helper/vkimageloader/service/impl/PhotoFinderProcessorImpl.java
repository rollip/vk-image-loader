package ru.helper.vkimageloader.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.client.wrapper.VkApiClientWrapper;
import ru.helper.vkimageloader.client.wrapper.VkAuthClientWrapper;
import ru.helper.vkimageloader.dto.business.PhotoToDownload;
import ru.helper.vkimageloader.dto.vk.HistoryAttachmentsResponse;
import ru.helper.vkimageloader.service.PhotoFinderProcessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoFinderProcessorImpl implements PhotoFinderProcessor {

    private final VkApiClientWrapper vkApiClientWrapper;
    private final VkAuthClientWrapper vkAuthClientWrapper;

    public List<PhotoToDownload> findPhotos(Long peerId) {
        String startFrom = "0";
        log.info("Start counting photos...");
        List<PhotoToDownload> photoToDownloads = new ArrayList<>();
        while (Objects.nonNull(startFrom)) {
            log.debug("Counting photos: {}", startFrom);
            HistoryAttachmentsResponse responseDto = vkApiClientWrapper.getHistoryAttachments(peerId, startFrom);

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
        log.info("Total photos: {}", photoToDownloads.size());
        return photoToDownloads;
    }

    private HistoryAttachmentsResponse.PhotoSize findBestQuality(List<HistoryAttachmentsResponse.PhotoSize> list) {
        return list.stream().max(Comparator.comparingInt(size -> size.height() * size.width())).orElse(null);
    }

    private PhotoToDownload toPhotoToDownload(HistoryAttachmentsResponse.Photo photo) {
        HistoryAttachmentsResponse.PhotoSize bestSize = photo.origPhoto() != null ? photo.origPhoto() : findBestQuality(photo.sizes());
        return new PhotoToDownload(photo.id(), photo.date(), bestSize.url());
    }
}
