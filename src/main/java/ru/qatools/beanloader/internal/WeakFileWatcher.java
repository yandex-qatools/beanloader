package ru.qatools.beanloader.internal;

import java.lang.ref.WeakReference;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class WeakFileWatcher extends FileWatcher {

    private final WeakReference<BeanLoadStrategy> loadStrategyReference;

    public WeakFileWatcher(BeanLoadStrategy loadStrategy, String directory, String file) {
        super(directory, file);
        this.loadStrategyReference = new WeakReference<>(loadStrategy);
    }

    @Override
    protected void invokeFileReload() {
        BeanLoadStrategy strategy = loadStrategyReference.get();
        if (strategy != null) {
            strategy.loadBean();
        }
    }
}
