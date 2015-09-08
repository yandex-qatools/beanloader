package ru.qatools.beanloader.internal;

import ru.qatools.beanloader.BeanChangeListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileWatcherLoadStrategy<T> extends FileLoadStrategy<T> implements FileChangeListener {

    private final Path directoryPath;
    private final String fileName;
    private final BeanChangeListener<T> listener;
    private final boolean preventGC;

    private ExecutorService executor;

    public FileWatcherLoadStrategy(String directory, String fileName) {
        this(directory, fileName, null);
    }

    public FileWatcherLoadStrategy(String directory, String fileName, BeanChangeListener<T> listener) {
        this(directory, fileName, listener, false);
    }

    public FileWatcherLoadStrategy(String directory, String fileName, BeanChangeListener<T> listener, boolean preventGC) {
        super(new File(directory, fileName), false);
        this.directoryPath = Paths.get(directory);
        this.fileName = fileName;
        this.listener = listener;
        this.preventGC = preventGC;
    }

    @Override
    public void init(Class<T> beanClass) {
        super.init(beanClass);
        fileChanged(directoryPath.resolve(fileName));
        startFileWatcherThread();
    }

    private void startFileWatcherThread() {
        FileChangeListener fileChangeListener = preventGC ? this : new WeakFileChangeListener(this);
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new FileWatcher(directoryPath, "glob:" + fileName, fileChangeListener));
        executor.shutdown();
    }

    @Override
    public void fileChanged(Path path) {
        loadBean();
        if (listener != null) {
            listener.beanChanged(path, getBean());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!preventGC && executor != null) {
            logger.debug("Strategy object is garbage-collected, stopping watcher thread");
            executor.shutdownNow();
        }
    }

    //static is crucial here: otherwise the back reference still exists!
    private static class WeakFileChangeListener implements FileChangeListener {

        private final WeakReference<FileChangeListener> listenerReference;

        public WeakFileChangeListener(FileChangeListener listener) {
            this.listenerReference = new WeakReference<>(listener);
        }

        @Override
        public void fileChanged(Path path) {
            FileChangeListener strategy = listenerReference.get();
            if (strategy != null) {
                strategy.fileChanged(path);
            }
        }
    }
}
