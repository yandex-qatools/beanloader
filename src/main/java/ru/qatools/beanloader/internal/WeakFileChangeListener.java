package ru.qatools.beanloader.internal;

import java.lang.ref.WeakReference;
import java.nio.file.Path;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class WeakFileChangeListener implements FileChangeListener {

    private final WeakReference<BeanLoadStrategy> loadStrategyReference;

    public WeakFileChangeListener(BeanLoadStrategy loadStrategy) {
        this.loadStrategyReference = new WeakReference<>(loadStrategy);
    }

    @Override
    public void fileChanged(Path path) {
        BeanLoadStrategy strategy = loadStrategyReference.get();
        if (strategy != null) {
            strategy.loadBean();
        }
    }
}
