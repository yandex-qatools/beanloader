package ru.qatools.beanloader.internal;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileWithWatcherLoadStrategy extends FileLoadStrategy {

    private final String directory;
    private final String file;

    private boolean fileWatcherInitialized;

    private ExecutorService executor;

    public FileWithWatcherLoadStrategy(String directory, String file) {
        super(new File(directory, file), false);
        this.directory = directory;
        this.file = file;
    }

    @Override
    protected synchronized void loadBean(Class beanClass) {
        super.loadBean(beanClass);
        if (!fileWatcherInitialized) {
            initFileWatcher(beanClass);
            fileWatcherInitialized = true;
        }
    }

    private void initFileWatcher(Class beanClass) {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new FileWatcher(this, beanClass, directory, file));
        executor.shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
        if (executor != null) {
            executor.shutdownNow();
        }
        super.finalize();
    }
}
