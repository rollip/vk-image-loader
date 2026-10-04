package ru.helper.vkimageloader.client.wrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.VkTokenHolder;
import ru.helper.vkimageloader.client.VkApiClient;
import ru.helper.vkimageloader.dto.vk.HistoryAttachmentsResponse;

@RequiredArgsConstructor
@Component
@Slf4j
public class VkApiClientWrapper {

    private final VkApiClient vkApiClient;
    private final VkTokenHolder vkTokenHolder;

    public HistoryAttachmentsResponse getHistoryAttachments(String startFrom) {
        log.info(String.valueOf(startFrom));
        return vkApiClient.getHistoryAttachments(
                "Bearer " + vkTokenHolder.getAccessToken(),
                vkTokenHolder.getAccessToken(),
                "https://dev.vk.ru",
                "5.199",
                2000000001L,
                "photo",
                200,
                1,
                startFrom);
    }


}
