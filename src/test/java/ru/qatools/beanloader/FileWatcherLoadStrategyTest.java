package ru.qatools.beanloader;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static ru.qatools.beanloader.BeanAssert.BEAN_XML_NAME;
import static ru.qatools.beanloader.BeanAssert.RESOURCES_DIR;
import static ru.qatools.beanloader.BeanAssert.getActualValue;
import static ru.qatools.beanloader.BeanAssert.setActualValue;
import static ru.qatools.beanloader.BeanLoader.load;
import static ru.qatools.beanloader.BeanLoaderStrategies.fileWithWatcher;
import static ru.qatools.beanloader.TestBeanChangeListener.beCalledWith;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.should;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.timeoutHasExpired;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileWatcherLoadStrategyTest extends BeanChangingTest {

    private TestBeanChangeListener listener;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        listener = new TestBeanChangeListener();
    }

    @Test
    public void testListenerIsCalledImmediately() throws Exception {
        load(Bean.class).from(fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME, listener));
        assertTrue(listener.isCalledWith(getActualValue()));
    }

    @Test
    public void testListenerIsNotCalledOnBeanGetter() {
        BeanLoader<Bean> beanLoader = load(Bean.class).from(fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME, listener));
        listener.reset();
        beanLoader.getBean();
        assertTrue(listener.isNotCalled());
    }

    @Test
    public void testListenerInvocation() throws Exception {
        //noinspection unused
        BeanLoader<Bean> beanLoader = load(Bean.class).from(fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME, listener));

        // I have no idea how this affects the test's result,
        // but without this line it's quite unstable on my machine.
        // Seems like we should wait after initializing WatchService,
        // but before writing a new value to the file
        Thread.sleep(1000);

        listener.reset();
        String newValue = "another " + getActualValue();
        setActualValue(newValue);
        assertThat(listener, should(beCalledWith(newValue))
                .whileWaitingUntil(timeoutHasExpired(60000)));
    }

    @Test
    public void testWatcherIsStoppedOnGarbageCollection() throws Exception {
        load(Bean.class).from(fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME, listener));
        listener.reset();
        System.gc();
        Thread.sleep(1000);
        String newValue = "another " + getActualValue();
        setActualValue(newValue);
        Thread.sleep(15000);
        assertTrue(listener.isNotCalled());
    }

    @Test
    public void testPreventGC() throws Exception {
        load(Bean.class).from(fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME, listener, true));
        listener.reset();
        System.gc();
        Thread.sleep(1000);
        String newValue = "one more another " + getActualValue();
        setActualValue(newValue);
        assertThat(listener, should(beCalledWith(newValue))
                .whileWaitingUntil(timeoutHasExpired(60000)));
    }
}
