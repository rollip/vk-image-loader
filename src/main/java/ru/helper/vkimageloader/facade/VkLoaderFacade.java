package ru.helper.vkimageloader.facade;

import ru.helper.vkimageloader.dto.business.ConversationToCli;

import java.io.IOException;
import java.util.List;

public interface VkLoaderFacade {

    void loadPhotos(Long chatToParse) throws IOException, InterruptedException;
    List<ConversationToCli> getConversations();
}
