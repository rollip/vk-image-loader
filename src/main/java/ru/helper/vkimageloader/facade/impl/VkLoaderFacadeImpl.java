package ru.helper.vkimageloader.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.client.wrapper.VkApiClientWrapper;
import ru.helper.vkimageloader.client.wrapper.VkAuthClientWrapper;
import ru.helper.vkimageloader.dto.business.ConversationToCli;
import ru.helper.vkimageloader.dto.business.PhotoToDownload;
import ru.helper.vkimageloader.facade.VkLoaderFacade;
import ru.helper.vkimageloader.service.PhotoDownloadProcessor;
import ru.helper.vkimageloader.service.PhotoFinderProcessor;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VkLoaderFacadeImpl implements VkLoaderFacade {

    private final VkApiClientWrapper vkApiClientWrapper;
    private final VkAuthClientWrapper vkAuthClientWrapper;
    private final PhotoDownloadProcessor photoDownloadProcessor;
    private final PhotoFinderProcessor photoFinderProcessor;

    public void loadPhotos(Long peerId) {
        List<PhotoToDownload> photos = photoFinderProcessor.findPhotos(peerId);
        photoDownloadProcessor.processAll(photos, peerId);
    }

    public List<ConversationToCli> getConversations(){
        vkAuthClientWrapper.refreshToken();
        return vkApiClientWrapper.getConversationsCli();
    }
}
