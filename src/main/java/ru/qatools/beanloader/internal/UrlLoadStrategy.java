package ru.qatools.beanloader.internal;

import javax.xml.bind.JAXB;
import java.net.URL;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class UrlLoadStrategy extends BeanLoadStrategy {

    protected final URL url;
    private final boolean reload;

    public UrlLoadStrategy(URL url, boolean reload) {
        super();
        this.url = url;
        this.reload = reload;
    }

    protected boolean canUnmarshal() {
        return url != null;
    }

    protected boolean reloadEveryTime() {
        return reload;
    }

    @Override
    protected Object performUnmarshal(Class beanClass) {
        return JAXB.unmarshal(url, beanClass);
    }

    @Override
    protected String getSourceDescription() {
        return url.toString();
    }
}
