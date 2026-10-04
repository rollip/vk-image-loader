package ru.helper.vkimageloader.dto.vk;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record HistoryAttachmentsResponse(
        Response response
) {

    public record Response(
            List<Item> items,
            @JsonProperty("next_from")
            String nextFrom
    ) {
    }

    public record Item(
            Attachment attachment,
            Long date,
            Long messageId,
            Long cmid,
            Long fromId,
            Integer position
    ) {
    }

    public record Attachment(
            String type,
            Photo photo
    ) {
    }

    public record Photo(
            @JsonProperty("album_id")
            Long albumId,
            Long date,
            Long id,
            @JsonProperty("owner_id")
            Long ownerId,
            @JsonProperty("access_key")
            String accessKey,
            List<PhotoSize> sizes,
            String text,
            @JsonProperty("web_view_token")
            String webViewToken,
            @JsonProperty("has_tags") Boolean hasTags,
            @JsonProperty("orig_photo") PhotoSize origPhoto) {
    }

    public record PhotoSize(
            Integer height,
            String type,
            Integer width,
            String url
    ) {
    }
}
