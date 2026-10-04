package ru.helper.vkimageloader.service;

import ru.helper.vkimageloader.dto.business.PhotoToDownload;

import java.util.List;

public interface PhotoFinderProcessor {

    List<PhotoToDownload> findPhotos(Long peerId);
}
