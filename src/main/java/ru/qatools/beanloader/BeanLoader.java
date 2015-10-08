package ru.qatools.beanloader;

import ru.qatools.beanloader.internal.BeanLoadStrategy;
import ru.qatools.beanloader.internal.FileChangeChainedListener;
import ru.qatools.beanloader.internal.FileChangeListener;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.newDirectoryStream;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class BeanLoader<T> {

    private final List<BeanLoadStrategy> strategies = new ArrayList<>();

    private final Class<T> beanClass;

    private BeanLoader(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public static <U> BeanLoader<U> load(Class<U> beanClass) {
        return new BeanLoader<>(beanClass);
    }

    @SuppressWarnings("unchecked")
    public BeanLoader<T> from(BeanLoadStrategy strategy) {
        strategy.init(beanClass);
        strategies.add(strategy);
        return this;
    }

    public T getBean() {
        for (BeanLoadStrategy strategy : strategies) {
            @SuppressWarnings("unchecked")
            T bean = (T) strategy.getBean();
            if (bean != null) {
                return bean;
            }
        }
        return null;
    }

    public static <U> void loadAll(Class<U> beanClass, String directory, String globPattern,
                                   BeanChangeListener<U> listener) throws IOException {
        loadAll(beanClass, Paths.get(directory), globPattern, listener);
    }
    public static <U> void loadAll(Class<U> beanClass, Path directory, String globPattern,
                                   BeanChangeListener<U> listener) throws IOException {
        FileChangeListener chainedListener = new FileChangeChainedListener<>(beanClass, listener);
        for (Path filePath : newDirectoryStream(directory, globPattern)) {
            chainedListener.fileChanged(filePath);
        }
    }
}
