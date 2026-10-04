package ru.helper.vkimageloader.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.dto.vk.TokenResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class VkAuthClient {

    @Value("${spring.client.vk.auth}")
    public String url;

    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public TokenResponse getToken(String cookie) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/?act=web_token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Origin", "https://vk.ru")
                .header("Cookie", cookie)
                .POST(HttpRequest.BodyPublishers.ofString(
                        "version=1&app_id=6287487"
                ))
                .build();

        var response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return objectMapper.readValue(response.body(), TokenResponse.class);
    }
}