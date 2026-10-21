package ru.helper.vkimageloader.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class DownloadSpeedCounter {

    private final AtomicLong existing = new AtomicLong();
    private final AtomicLong downloaded = new AtomicLong();
    private final AtomicLong failed = new AtomicLong();
    private final AtomicLong total = new AtomicLong();
    private final AtomicBoolean loggingEnabled = new AtomicBoolean();

    private ScheduledFuture<?> loggingTask;

    private final long startTime = System.nanoTime();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public void start(int size) {

        if (Objects.nonNull(loggingTask) && !loggingTask.isDone()) {
            loggingTask.cancel(false);
        }

        downloaded.set(0);
        failed.set(0);
        existing.set(0);
        total.set(size);
        loggingEnabled.set(true);

        loggingTask = scheduler.scheduleAtFixedRate(this::logSpeed,5,5,TimeUnit.SECONDS);
    }

    public void incrementDownloaded() {
        downloaded.incrementAndGet();
    }

    public void incrementFailed() {
        failed.incrementAndGet();
    }

    public void incrementExisting() {
        existing.incrementAndGet();
    }

    private void logSpeed() {
        if (!loggingEnabled.get()) {
            return;
        }

        long pTotal = total.get();
        long pDownloaded = downloaded.get();
        long pFailed = failed.get();
        long pExisting = existing.get();
        long processed = pDownloaded + pFailed + pExisting;

        long elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000;
        double filesPerSecond = (double) processed / elapsedSeconds;
        double percentage = (double) pDownloaded / pTotal * 100.0;
        double percentageFailed = (double) pFailed / pTotal * 100.0;

        long remainingFiles = pTotal - pDownloaded - pFailed - pExisting;
        int totalSeconds = (int) (remainingFiles / filesPerSecond);
        int etaMin = totalSeconds / 60;
        int etaSec = totalSeconds % 60;

        log.info(
                "Download: {} files/sec, downloaded {}/{} ({}%), failed:{} ({}%), ETA: {} min {} sec",
                String.format("%.2f", filesPerSecond),
                pDownloaded + pExisting,
                pTotal,
                String.format("%.2f", percentage),
                pFailed,
                String.format("%.2f", percentageFailed),
                etaMin,
                etaSec
        );
    }

    public void stop() {
        if (!loggingEnabled.getAndSet(false)) {
            return;
        }

        if (Objects.nonNull(loggingTask)) {
            loggingTask.cancel(false);
            loggingTask = null;
        }

        logSpeed();
    }
}
