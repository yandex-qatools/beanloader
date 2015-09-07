package ru.qatools.beanloader.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 * @author Dmitry Baev charlie@yandex-team.ru
 */
public class FileWatcher implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WeakReference<BeanLoadStrategy> loadStrategyReference;
    private final String directory;
    private final String file;

    public FileWatcher(BeanLoadStrategy loadStrategy, String directory, String file) {
        this.loadStrategyReference = new WeakReference<>(loadStrategy);
        this.directory = directory;
        this.file = file;
    }

    @Override
    public void run() {
        Path path = Paths.get(directory);
        try (WatchService service = FileSystems.getDefault().newWatchService()) {
            path.register(service, ENTRY_MODIFY);
            watch(service);
        } catch (IOException e) {
            logger.error("Can't create watch service for directory " + directory, e);
        } catch (InterruptedException e) {
            logger.warn("oops, thread was interrupted");
        }
    }

    private void watch(WatchService service) throws InterruptedException {
        logger.info("Watching for changes in directory " + directory);
        //noinspection all
        while (true) {
            WatchKey key = service.take();
            handleKey(key);
            key.reset();
        }
    }

    private void handleKey(WatchKey key) {
        for (WatchEvent event : key.pollEvents()) {
            Path path = (Path) event.context();
            if (path.toAbsolutePath().endsWith(file)) {
                logger.info("file '" + file + "' changed");
                invokeFileReload();
            }
        }
    }

    protected void invokeFileReload() {
        BeanLoadStrategy strategy = loadStrategyReference.get();
        if (strategy != null) {
            strategy.loadBean();
        }
    }
}
