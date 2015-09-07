package ru.qatools.beanloader;

import ru.qatools.beanloader.internal.BeanLoadStrategy;
import ru.qatools.beanloader.internal.FileLoadStrategy;
import ru.qatools.beanloader.internal.FileWatcherLoadStrategy;
import ru.qatools.beanloader.internal.ResourceLoadStrategy;
import ru.qatools.beanloader.internal.UrlLoadStrategy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class BeanLoaderStrategies {

    private BeanLoaderStrategies() {
    }

    public static FileLoadStrategy file(String filename) {
        return file(filename, false);
    }

    public static FileLoadStrategy file(File file) {
        return file(file, false);
    }

    public static FileLoadStrategy file(String filename, boolean reloadEveryTime) {
        return file(new File(filename), reloadEveryTime);
    }

    public static FileLoadStrategy file(File file, boolean reloadEveryTime) {
        return new FileLoadStrategy(file, reloadEveryTime);
    }

    public static FileWatcherLoadStrategy fileWithWatcher(String directoryToWatch, String fileToWatch) {
        return new FileWatcherLoadStrategy(directoryToWatch, fileToWatch);
    }

    public static <T> FileWatcherLoadStrategy<T> fileWithWatcher(String directoryToWatch, String fileToWatch, BeanChangeListener<T> listener) {
        return new FileWatcherLoadStrategy<>(directoryToWatch, fileToWatch, listener);
    }

    public static UrlLoadStrategy url(URL url) {
        return url(url, false);
    }

    static BeanLoadStrategy url(String url) throws MalformedURLException {
        return url(new URL(url));
    }

    static BeanLoadStrategy url(String url, boolean reloadEveryTime) throws MalformedURLException {
        return url(new URL(url), reloadEveryTime);
    }

    public static UrlLoadStrategy url(URL url, boolean reloadEveryTime) {
        return new UrlLoadStrategy(url, reloadEveryTime);
    }

    public static ResourceLoadStrategy resource(String resource) {
        return new ResourceLoadStrategy(resource);
    }
}
