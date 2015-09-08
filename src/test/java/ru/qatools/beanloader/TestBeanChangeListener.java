package ru.qatools.beanloader;

import java.nio.file.Path;

public class TestBeanChangeListener extends TestListener implements BeanChangeListener<Bean> {

    @Override
    public void beanChanged(Path path, Bean newBean) {
        update(newBean.getValue());
    }
}
