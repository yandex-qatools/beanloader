package ru.qatools.beanloader.internal;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class ImmortalFileWatcher extends FileWatcher {

    private final BeanLoadStrategy loadStrategy;

    public ImmortalFileWatcher(BeanLoadStrategy loadStrategy, String directory, String file) {
        super(directory, file);
        this.loadStrategy = loadStrategy;
    }

    @Override
    protected void fileChanged() {
        loadStrategy.loadBean();
    }
}
