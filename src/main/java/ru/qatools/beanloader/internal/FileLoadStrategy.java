package ru.qatools.beanloader.internal;

import javax.xml.bind.JAXB;
import java.io.File;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileLoadStrategy extends BeanLoadStrategy {

    private final File file;
    private final boolean reload;

    public FileLoadStrategy(File file, boolean reload) {
        super();
        this.file = file;
        this.reload = reload;
    }

    @Override
    protected boolean canUnmarshal() {
        return file.exists();
    }

    protected boolean reloadEveryTime() {
        return reload;
    }

    @Override
    protected Object performUnmarshal(Class beanClass) {
        return JAXB.unmarshal(file, beanClass);
    }

    @Override
    protected String getSourceDescription() {
        return "file '" + file.getPath() + "'";
    }
}