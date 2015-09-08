package ru.qatools.beanloader.internal;

import java.nio.file.Path;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class StrongFileChangeListener implements FileChangeListener {

    private final BeanLoadStrategy loadStrategy;

    public StrongFileChangeListener(BeanLoadStrategy loadStrategy) {
        this.loadStrategy = loadStrategy;
    }

    @Override
    public void fileChanged(Path path) {
        loadStrategy.loadBean();
    }
}
