package ru.qatools.beanloader.internal;

import ru.qatools.beanloader.BeanChangeListener;

import java.nio.file.Path;

import static ru.qatools.beanloader.BeanLoader.load;
import static ru.qatools.beanloader.BeanLoaderStrategies.file;

public class FileChangeChainedListener<T> implements FileChangeListener {

    private final Class<?> beanClass;
    private final BeanChangeListener<T> listener;

    public FileChangeChainedListener(Class<?> beanClass, BeanChangeListener<T> listener) {
        this.beanClass = beanClass;
        this.listener = listener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fileChanged(Path path) {
        listener.beanChanged(path, (T) load(beanClass).from(file(path.toFile())).getBean());
    }
}
