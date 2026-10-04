package ru.helper.vkimageloader.client.wrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.helper.VkTokenHolder;
import ru.helper.vkimageloader.client.VkApiClient;
import ru.helper.vkimageloader.dto.business.ConversationToCli;
import ru.helper.vkimageloader.dto.vk.ConversionsResponse;
import ru.helper.vkimageloader.dto.vk.HistoryAttachmentsResponse;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class VkApiClientWrapper {

    private final VkApiClient vkApiClient;
    private final VkTokenHolder vkTokenHolder;
    private static final String BEARER_PREFIX = "Bearer ";

    public HistoryAttachmentsResponse getHistoryAttachments(Long peerId, String startFrom) {
        log.info(String.valueOf(startFrom));
        return vkApiClient.getHistoryAttachments(
                BEARER_PREFIX + vkTokenHolder.getAccessToken(),
                vkTokenHolder.getAccessToken(),
                "https://dev.vk.ru",
                "5.199",
                peerId,
                "photo",
                200,
                1,
                startFrom);
    }

    public List<ConversationToCli> getConversationsCli() {
        ConversionsResponse conversations = vkApiClient.getConversations(
                "Bearer " + vkTokenHolder.getAccessToken(),
                vkTokenHolder.getAccessToken(),
                "https://dev.vk.ru",
                "5.199",
                200,
                1);
        return conversations.response().items().stream()
                .map(i-> {
                    ConversionsResponse.Conversation conversation = i.conversation();
                    ConversionsResponse.ChatSettings chatSettings = conversation.chatSettings();
                    // TODO: узнать откуда брать имя чела если это не групповой чат
                    return new ConversationToCli(conversation.peer().id(), Objects.nonNull(chatSettings) ? chatSettings.title() : "UNKNOWN");
                })
                .toList();
    }
}
