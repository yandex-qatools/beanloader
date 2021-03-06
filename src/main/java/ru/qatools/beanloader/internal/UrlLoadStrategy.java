package ru.qatools.beanloader.internal;

import javax.xml.bind.JAXB;
import java.net.URL;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class UrlLoadStrategy<T> extends BeanLoadStrategy<T> {

    protected final URL url;
    private final boolean reload;

    public UrlLoadStrategy(URL url, boolean reload) {
        super();
        this.url = url;
        this.reload = reload;
    }

    @Override
    protected boolean canUnmarshal() {
        return url != null;
    }

    @Override
    protected boolean reloadEveryTime() {
        return reload;
    }

    @Override
    protected T performUnmarshal(Class<T> beanClass) {
        return JAXB.unmarshal(url, beanClass);
    }

    @Override
    protected String getSourceDescription() {
        return url.toString();
    }
}
