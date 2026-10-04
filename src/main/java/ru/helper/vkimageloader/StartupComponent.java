package ru.helper.vkimageloader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.dto.business.ConversationToCli;
import ru.helper.vkimageloader.facade.VkLoaderFacade;
import ru.helper.vkimageloader.helper.VkTokenHolder;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupComponent {

    public static final String EXIT = "exit";
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
        log.info("Available chats: ");
        List<ConversationToCli> conversations = vkLoaderFacade.getConversations();
        String chatToParse;
        do {
            conversations.forEach(c -> {
                if (!"UNKNOWN".equals(c.title())) {
                    log.info("Title: {}, peerId: {}", c.title(), c.peerId());
                }
            });

            log.info("Enter chat to parse: ");
            chatToParse = scanner.nextLine().trim();

            if (chatToParse.isBlank()) {
                throw new IllegalArgumentException("ChatToParse cannot be empty");
            }

            log.info("Start parsing...");
            vkLoaderFacade.loadPhotos(Long.parseLong(chatToParse));
            log.info("Parsing finished.");
        } while (!(EXIT.equals(chatToParse)));
    }
}