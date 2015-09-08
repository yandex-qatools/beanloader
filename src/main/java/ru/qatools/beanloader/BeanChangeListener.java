package ru.qatools.beanloader;

import java.nio.file.Path;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public interface BeanChangeListener<T> {

    void beanChanged(Path path, T newBean);
}
