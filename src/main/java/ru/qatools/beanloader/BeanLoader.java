package ru.qatools.beanloader;

import ru.qatools.beanloader.internal.BeanLoadStrategy;

import java.util.ArrayList;
import java.util.List;

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

    public BeanLoader<T> from(BeanLoadStrategy strategy) {
        strategies.add(strategy);
        return this;
    }

    public T getBean() {
        for (BeanLoadStrategy strategy : strategies) {
            @SuppressWarnings("unchecked")
            T bean = (T) strategy.getBeanAs(beanClass);
            if (bean != null) {
                return bean;
            }
        }
        return null;
    }
}
