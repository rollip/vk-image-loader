package ru.helper.vkimageloader.service;

import ru.helper.vkimageloader.dto.business.PhotoToDownload;

import java.util.List;

public interface PhotoDownloadProcessor {

    void processAll(List<PhotoToDownload> photoToDownloads, Long peerId);
}
