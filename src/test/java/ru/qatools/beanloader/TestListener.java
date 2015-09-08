package ru.qatools.beanloader;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class TestListener {

    private String newValue;

    public void reset() {
        update(null);
    }

    public void update(String value) {
        newValue = value;
    }

    public boolean isCalledWith(String value) {
        return value.equals(newValue);
    }

    public boolean isNotCalled() {
        return newValue == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{newValue='" + newValue + "'}";
    }

    public static Matcher<TestListener> beCalledWith(final String value) {
        return new TypeSafeMatcher<TestListener>() {
            @Override
            protected boolean matchesSafely(TestListener listener) {
                return listener.isCalledWith(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("not called with value '" + value + "'");
            }
        };
    }
}
