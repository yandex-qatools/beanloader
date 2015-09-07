package ru.qatools.beanloader;

import static ru.qatools.beanloader.BeanLoader.load;
import static ru.qatools.beanloader.BeanLoaderStrategies.fileWithWatcher;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class BeanWatcher {

    private BeanWatcher() {
    }

    public static <T> void watchFor(Class<T> beanClass, String directory, String filename, BeanChangeListener<T> listener) {
        load(beanClass).from(fileWithWatcher(directory, filename, listener, true));
    }
}
