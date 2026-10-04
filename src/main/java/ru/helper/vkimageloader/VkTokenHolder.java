package ru.helper.vkimageloader;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class VkTokenHolder {
    private String refreshToken;
    private String accessToken;
    private String cookie;
}
