package ru.helper.vkimageloader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.facade.VkLoaderFacade;

import java.io.IOException;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupComponent {

    private final VkTokenHolder tokenHolder;
    private final VkLoaderFacade vkLoaderFacade;

    @EventListener(ApplicationReadyEvent.class)
    public void start() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        log.info("=== VK Downloader ===");
        log.info("Enter VK cookie: ");

        String cookie = scanner.nextLine().trim();

        if (cookie.isBlank()) {
            throw new IllegalArgumentException("VK cookie cannot be empty");
        }

        tokenHolder.setCookie(cookie);

        log.info("Cookie accepted.");
        log.info("Starting downloader...");
        vkLoaderFacade.loadPhotos();
    }
}