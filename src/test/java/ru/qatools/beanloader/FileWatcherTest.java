package ru.qatools.beanloader;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import ru.qatools.beanloader.internal.FileWatcher;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static ru.qatools.beanloader.BeanAssert.BEAN_XML_NAME;
import static ru.qatools.beanloader.BeanAssert.RESOURCES_DIR;
import static ru.qatools.beanloader.BeanAssert.getActualValue;
import static ru.qatools.beanloader.BeanAssert.setActualValue;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.should;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.timeoutHasExpired;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileWatcherTest extends BeanChangingTest {

    @Test
    public void testFileWatcher() throws Exception {
        final AtomicBoolean unmarshalInvoked = new AtomicBoolean(false);
        FileWatcher watcher = new FileWatcher(RESOURCES_DIR, BEAN_XML_NAME) {
            protected void fileChanged() {
                unmarshalInvoked.getAndSet(true);
            }
        };
        Executors.newSingleThreadExecutor().submit(watcher);

        Thread.sleep(1000);
        assertFalse(unmarshalInvoked.get());
        setActualValue("another " + getActualValue());
        assertThat(unmarshalInvoked, should(beTrue())
                .whileWaitingUntil(timeoutHasExpired(60000)));
    }

    private static Matcher<AtomicBoolean> beTrue() {
        return new TypeSafeMatcher<AtomicBoolean>() {
            @Override
            protected boolean matchesSafely(AtomicBoolean item) {
                return item.get();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("<true>");
            }
        };
    }
}
