package ru.helper.vkimageloader.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.helper.vkimageloader.dto.vk.HistoryAttachmentsResponse;

@FeignClient(name = "vk-api", url = "${vk.api}")
public interface VkApiClient {

    @PostMapping(
            value = "/method/messages.getHistoryAttachments",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    HistoryAttachmentsResponse getHistoryAttachments(
            @RequestHeader("authorization") String authorization,
            @RequestHeader("access_token") String accessToken,
            @RequestHeader("origin") String origin,
            @RequestParam("v") String version,
            @RequestParam("peer_id") long peerId,
            @RequestParam("media_type") String mediaType,
            @RequestParam("count") int count,
            @RequestParam("photo_sizes") int photoSizes,
            @RequestParam(value = "start_from", required = false) String startFrom
    );
}