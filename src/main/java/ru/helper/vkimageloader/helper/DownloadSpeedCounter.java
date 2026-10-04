package ru.helper.vkimageloader.helper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class DownloadSpeedCounter {

    private final AtomicLong files = new AtomicLong();
    private final AtomicLong totalFiles = new AtomicLong();

    private final long startTime = System.nanoTime();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    void start() {
        scheduler.scheduleAtFixedRate(this::logSpeed, 5, 5, TimeUnit.SECONDS);
    }

    public void setTotalFiles(long totalFiles) {
        this.totalFiles.set(totalFiles);
    }

    public void increment() {
        files.incrementAndGet();
    }

    public void decrementTotal() {
        totalFiles.decrementAndGet();
    }


    private void logSpeed() {

        long currentFiles = files.get();
        long total = totalFiles.get();
        double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
        double filesPerSecond = currentFiles / elapsedSeconds;
        double percentage = (double) currentFiles / total * 100.0;
        double remainingFiles = total - currentFiles;
        double eta = remainingFiles / filesPerSecond / 60;

        log.info(
                "Download: {} files/sec, files {} / {} ({}%), ETA: {} min",
                String.format("%.2f", filesPerSecond),
                currentFiles,
                total,
                String.format("%.2f", percentage),
                String.format("%.2f", eta)
        );
    }

    @PreDestroy
    void stop() {
        scheduler.shutdownNow();
    }
}
