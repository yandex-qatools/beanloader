package ru.qatools.beanloader;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.nio.file.Path;

public class TestBeanChangeListener implements BeanChangeListener<Bean> {

    private String newValue;

    public boolean isCalledWith(String value) {
        return value.equals(newValue);
    }

    public boolean isNotCalled() {
        return newValue == null;
    }

    public void reset() {
        newValue = null;
    }

    @Override
    public void beanChanged(Path path, Bean newBean) {
        newValue = newBean.getValue();
    }

    @Override
    public String toString() {
        return String.format("TestBeanChangeListener{newValue='%s'}", newValue);
    }

    public static Matcher<? super TestBeanChangeListener> beCalledWith(final String value) {
        return new TypeSafeMatcher<TestBeanChangeListener>() {
            @Override
            protected boolean matchesSafely(TestBeanChangeListener listener) {
                return listener.isCalledWith(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("not called with value '" + value + "'");
            }
        };
    }
}
