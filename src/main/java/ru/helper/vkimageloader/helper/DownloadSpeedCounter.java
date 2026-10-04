package ru.helper.vkimageloader.helper;

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

    public void start() {
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
        long elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000;
        double filesPerSecond = (double) currentFiles / elapsedSeconds;
        double percentage = (double) currentFiles / total * 100.0;

        long remainingFiles = total - currentFiles;
        int totalSeconds = (int) (remainingFiles / filesPerSecond);
        int etaMin = totalSeconds / 60;
        int etaSec = totalSeconds % 60;

        log.info(
                "Download: {} files/sec, files {} / {} ({}%), ETA: {} min {} sec",
                String.format("%.2f", filesPerSecond),
                currentFiles,
                total,
                String.format("%.2f", percentage),
                etaMin,
                etaSec
        );
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
