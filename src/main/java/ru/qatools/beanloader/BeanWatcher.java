package ru.qatools.beanloader;

import ru.qatools.beanloader.internal.FileChangeChainedListener;
import ru.qatools.beanloader.internal.FileWatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class BeanWatcher {

    private BeanWatcher() {
    }

    public static <T> void watchFor(final Class<T> beanClass, String directory, String globPattern,
                                    final BeanChangeListener<T> listener) throws IOException {
        watchFor(beanClass, Paths.get(directory), globPattern, listener);
    }

    public static <T> void watchFor(final Class<T> beanClass, Path directory, String globPattern,
                                    final BeanChangeListener<T> listener) throws IOException {
        BeanLoader.loadAll(beanClass, directory, globPattern, listener);

        ExecutorService executor = newSingleThreadExecutor();
        executor.execute(new FileWatcher(
                directory,
                "glob:" + globPattern,
                new FileChangeChainedListener<>(beanClass, listener)
        ));
        executor.shutdown();
    }
}
