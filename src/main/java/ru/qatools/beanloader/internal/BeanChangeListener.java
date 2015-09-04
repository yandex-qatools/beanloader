package ru.qatools.beanloader.internal;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public interface BeanChangeListener<T> {

    void beanChanged(T newBean);
}
