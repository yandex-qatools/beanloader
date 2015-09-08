package ru.qatools.beanloader.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DataBindingException;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public abstract class BeanLoadStrategy<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private T bean;
    private Class<T> beanClass;

    private boolean loaded;

    public void init(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public final T getBean() {
        if (!loaded || reloadEveryTime()) {
            loadBean();
        }
        return bean;
    }

    protected final void loadBean() {
        if (beanClass == null) {
            throw new UnsupportedOperationException("Unable to load bean: call init(beanClass) first!");
        }

        loaded = true;
        logger.trace("trying to load bean from " + getSourceDescription());
        if (!canUnmarshal()) {
            logger.trace("source does not exist, aborting");
            return;
        }

        try {
            logger.trace("source exists, trying to unmarshal");
            bean = performUnmarshal(beanClass);
            logger.trace("successfully unmarshalled");
        } catch (DataBindingException e) {
            logUnmarshallingException(e, getSourceDescription());
        }
    }

    private void logUnmarshallingException(Exception e, String sourceDescription) {
        if (logger.isDebugEnabled()) {
            logger.error("exception caught while unmarshalling from " + sourceDescription, e);
        } else {
            logger.error(String.format("exception caught while unmarshalling from %s: %s",
                    sourceDescription, e.getMessage()));
        }
    }

    protected abstract boolean canUnmarshal();
    protected abstract boolean reloadEveryTime();
    protected abstract T performUnmarshal(Class beanClass);
    protected abstract String getSourceDescription();
}
