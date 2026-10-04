package ru.helper.vkimageloader.dto.vk;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ConversionsResponse(Response response) {

    public record Response(
            Integer count,
            List<Item> items) {
    }

    public record Item(
            Conversation conversation) {
    }

    public record Conversation(
            Peer peer,
            @JsonProperty("chat_settings")
            ChatSettings chatSettings) {
    }

    public record Peer(Long id,
                       String type,
                       @JsonProperty("local_id")
                       Long localId) {
    }

    public record ChatSettings(String title) {
    }
}
