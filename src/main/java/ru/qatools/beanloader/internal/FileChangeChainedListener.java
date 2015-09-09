package ru.qatools.beanloader.internal;

import ru.qatools.beanloader.BeanChangeListener;

import javax.xml.bind.JAXB;
import java.nio.file.Path;

public class FileChangeChainedListener<T> implements FileChangeListener {

    private final Class<T> beanClass;
    private final BeanChangeListener<T> listener;

    public FileChangeChainedListener(Class<T> beanClass, BeanChangeListener<T> listener) {
        this.beanClass = beanClass;
        this.listener = listener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fileChanged(Path path) {
        listener.beanChanged(path, JAXB.unmarshal(path.toFile(), beanClass));
    }
}
