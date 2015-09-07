package ru.qatools.beanloader;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static ru.qatools.beanloader.BeanAssert.BEAN_XML_NAME;
import static ru.qatools.beanloader.BeanAssert.RESOURCES_DIR;
import static ru.qatools.beanloader.BeanAssert.getActualValue;
import static ru.qatools.beanloader.BeanAssert.setActualValue;
import static ru.qatools.beanloader.BeanWatcher.watchFor;
import static ru.qatools.beanloader.TestBeanChangeListener.beCalledWith;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.should;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.timeoutHasExpired;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class BeanWatcherTest extends BeanChangingTest {

    @Test
    public void testWatcherIsNotStoppedOnGarbageCollection() throws Exception {
        TestBeanChangeListener listener = new TestBeanChangeListener();
        watchFor(Bean.class, RESOURCES_DIR, BEAN_XML_NAME, listener);
        listener.reset();

        // I have no idea how this affects the test's result,
        // but without this line it's quite unstable on my machine.
        // Seems like we should wait after initializing WatchService,
        // but before writing a new value to the file
        Thread.sleep(1000);

        System.gc();
        String newValue = "another " + getActualValue();
        setActualValue(newValue);
        assertThat(listener, should(beCalledWith(newValue))
                .whileWaitingUntil(timeoutHasExpired(60000)));
    }
}
