package ru.helper.vkimageloader.client.wrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.VkTokenHolder;
import ru.helper.vkimageloader.client.VkAuthClient;
import ru.helper.vkimageloader.dto.vk.TokenResponse;

import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class VkAuthClientWrapper {

    private final VkAuthClient vkAuthClient;
    private final VkTokenHolder vkTokenHolder;

    public void refreshToken() {
        TokenResponse accessToken;
        log.info("Refreshing access token");
        try {
            accessToken = vkAuthClient.getToken(vkTokenHolder.getCookie());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (Objects.nonNull(accessToken.error_info())) {
            log.error(accessToken.error_info());
            throw new RuntimeException(accessToken.error_info());
        }
        if (Objects.nonNull(accessToken.data())) {
            vkTokenHolder.setAccessToken(accessToken.data().accessToken());
        }
    }

}