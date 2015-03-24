package ru.qatools.beanloader.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 * @author Dmitry Baev charlie@yandex-team.ru
 */
public class FileWatcher implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BeanLoadStrategy loadStrategy;
    private final Class beanClass;
    private final String directory;
    private final String file;

    public FileWatcher(BeanLoadStrategy loadStrategy, Class beanClass,
                       String directory, String file) {
        this.loadStrategy = loadStrategy;
        this.beanClass = beanClass;
        this.directory = directory;
        this.file = file;
    }

    @Override
    public void run() {
        Path directory = Paths.get(this.directory);
        try (WatchService service = FileSystems.getDefault().newWatchService()) {
            directory.register(service, StandardWatchEventKinds.ENTRY_MODIFY);
            watch(service);
        } catch (IOException e) {
            logger.error("Can't create watch service for directory " + this.directory, e);
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
        loadStrategy.loadBean(beanClass);
    }
}
