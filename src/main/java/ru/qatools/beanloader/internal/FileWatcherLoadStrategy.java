package ru.qatools.beanloader.internal;

import ru.qatools.beanloader.BeanChangeListener;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileWatcherLoadStrategy<T> extends FileLoadStrategy<T> {

    private final String directory;
    private final String file;
    private final BeanChangeListener<T> listener;
    private final boolean preventGC;

    private ExecutorService executor;

    public FileWatcherLoadStrategy(String directory, String file) {
        this(directory, file, new BeanChangeListener<T>() {
            @Override
            public void beanChanged(T newBean) {
            }
        });
    }

    public FileWatcherLoadStrategy(String directory, String file, BeanChangeListener<T> listener) {
        this(directory, file, listener, false);
    }

    public FileWatcherLoadStrategy(String directory, String file, BeanChangeListener<T> listener, boolean preventGC) {
        super(new File(directory, file), false);
        this.directory = directory;
        this.file = file;
        this.listener = listener;
        this.preventGC = preventGC;
    }

    @Override
    public void init(Class<T> beanClass) {
        super.init(beanClass);
        loadBean();
        startFileWatcherThread();
    }

    private void startFileWatcherThread() {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(preventGC ? new ImmortalFileWatcher(this, directory, file)
                                   : new WeakFileWatcher    (this, directory, file));
        executor.shutdown();
    }

    @Override
    protected synchronized void loadBean() {
        super.loadBean();
        listener.beanChanged(getBean());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!preventGC && executor != null) {
            logger.debug("Strategy object is garbage-collected, stopping watcher thread");
            executor.shutdownNow();
        }
    }
}
