package ru.helper.vkimageloader.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class PhotoDownloadClient {

    private final RestClient restClient = RestClient.create();

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 3)
    )
    public byte[] download(String url) {
        return restClient.get().uri(url).retrieve().body(byte[].class);
    }
}