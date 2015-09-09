package ru.qatools.beanloader.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 * @author Dmitry Baev charlie@yandex-team.ru
 */
public class FileWatcher implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcher.class);

    private final Path directoryPath;
    private final String pathMatcherPattern;
    private final FileChangeListener listener;

    public FileWatcher(Path directoryPath, String pathMatcherPattern, FileChangeListener listener) {
        this.directoryPath = directoryPath;
        this.pathMatcherPattern = pathMatcherPattern;
        this.listener = listener;
    }

    @Override
    public void run() {
        FileSystem fileSystem = FileSystems.getDefault();
        try (WatchService service = fileSystem.newWatchService()) {
            directoryPath.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            watch(service, fileSystem.getPathMatcher(pathMatcherPattern));
        } catch (IOException e) {
            LOGGER.error("Can't create watch service for directory {}", directoryPath, e);
        } catch (InterruptedException e) {
            LOGGER.warn("oops, thread was interrupted");
        }
    }

    private void watch(WatchService service, PathMatcher pathMatcher) throws InterruptedException {
        LOGGER.info("Watching for changes in directory {}", directoryPath);
        //noinspection InfiniteLoopStatement
        while (true) {
            WatchKey key = service.take();
            handleKey(key, pathMatcher);
            key.reset();
        }
    }

    private void handleKey(WatchKey key, PathMatcher pathMatcher) {
        for (WatchEvent event : key.pollEvents()) {
            Path fileRelativePath = (Path) event.context();
            if (pathMatcher.matches(fileRelativePath)) {
                Path fileResolvedPath = directoryPath.resolve(fileRelativePath);
                LOGGER.info("file {} changed", fileResolvedPath);
                listener.fileChanged(fileResolvedPath);
            }
        }
    }
}
