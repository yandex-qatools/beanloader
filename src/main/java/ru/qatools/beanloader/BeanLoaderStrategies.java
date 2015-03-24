package ru.qatools.beanloader;

import ru.qatools.beanloader.internal.*;

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

    public static FileLoadStrategy file(String filename, boolean reloadEveryTime) {
        return file(new File(filename), reloadEveryTime);
    }

    public static FileLoadStrategy file(File file, boolean reloadEveryTime) {
        return new FileLoadStrategy(file, reloadEveryTime);
    }

    public static FileWithWatcherLoadStrategy fileWithWatcher(String directoryToWatch, String fileToWatch) {
        return new FileWithWatcherLoadStrategy(directoryToWatch, fileToWatch);
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
