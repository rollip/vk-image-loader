package ru.helper.vkimageloader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.helper.vkimageloader.facade.VkLoaderFacade;
import ru.helper.vkimageloader.helper.VkTokenHolder;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
@Slf4j
public class CliRunner implements CommandLineRunner {

    public static final String EXIT = "exit";
    private final VkTokenHolder tokenHolder;
    private final VkLoaderFacade vkLoaderFacade;

    @Override
    public void run(String... args) {
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

        String chatToParse;
        do {
            log.info("Enter peerId to parse (or type 'exit' to quit): ");
            chatToParse = scanner.nextLine().trim();

            if (chatToParse.isBlank()) {
                log.warn("ChatToParse cannot be empty. Try again.");
                continue;
            }

            if (EXIT.equalsIgnoreCase(chatToParse)) {
                break;
            }

            log.info("Start parsing for peerId: {}...", chatToParse);
            try {
                vkLoaderFacade.loadPhotos(Long.parseLong(chatToParse));
                log.info("Parsing finished.");
            } catch (NumberFormatException e) {
                log.error("Invalid peerId format. Please enter a valid number.");
            } catch (Exception e) {
                log.error("Error occurred during parsing: {}", e.getMessage(), e);
            }

        } while (true);

        log.info("Exiting application...");
    }
}