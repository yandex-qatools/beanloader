package ru.qatools.beanloader;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class TestListener {

    private List<String> values;

    public TestListener() {
        reset();
    }

    public void reset() {
        values = new ArrayList<>();
    }

    public void update(String value) {
        values.add(value);
    }

    public boolean isCalledWith(String value) {
        return values.contains(value);
    }

    public boolean isNotCalled() {
        return values.isEmpty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{values='" + values + "'}";
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
