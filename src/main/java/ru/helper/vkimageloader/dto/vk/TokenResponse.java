package ru.helper.vkimageloader.dto.vk;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        String type,
        String error_info,
        Data data
) {
    public record Data(
            @JsonProperty("access_token")
            String accessToken,
            Long expires,
            @JsonProperty("user_id")
            Long userId,
            @JsonProperty("logout_hash")
            String logoutHash
    ) {
    }
}
