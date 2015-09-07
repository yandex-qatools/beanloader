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

    private ExecutorService executor;

    public FileWatcherLoadStrategy(String directory, String file) {
        this(directory, file, new BeanChangeListener<T>() {
            @Override
            public void beanChanged(T newBean) {
            }
        });
    }

    public FileWatcherLoadStrategy(String directory, String file, BeanChangeListener<T> listener) {
        super(new File(directory, file), false);
        this.directory = directory;
        this.file = file;
        this.listener = listener;
    }

    @Override
    public void init(Class<T> beanClass) {
        super.init(beanClass);
        loadBean();
        startFileWatcherThread();
    }

    private void startFileWatcherThread() {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new FileWatcher(this, directory, file));
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
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
