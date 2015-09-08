package ru.qatools.beanloader;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
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

    private TestBeanChangeListener listener;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        listener = new TestBeanChangeListener();
    }

    @Test
    public void testListenerIsInvokedImmediatelyForExistingFiles() throws Exception {
        String value1 = createNewFile();
        String value2 = createNewFile();
        watchFor(Bean.class, RESOURCES_DIR, "*.xml", listener);
        assertTrue(listener.isCalledWith(getActualValue()));
        assertTrue(listener.isCalledWith(value1));
        assertTrue(listener.isCalledWith(value2));
    }

    @Test
    public void testFilesByPatternWatcher() throws Exception {
        watchFor(Bean.class, RESOURCES_DIR, "*.xml", listener);
        listener.reset();

        // I have no idea how this affects the test's result,
        // but without this line it's quite unstable on my machine.
        // Seems like we should wait after initializing WatchService,
        // but before writing a new value to the file
        Thread.sleep(1000);

        assertListenerIsCalledForANewFile();
        assertListenerIsCalledForANewFile();
    }

    @Test
    public void testWatcherIsNotStoppedOnGarbageCollection() throws Exception {
        watchFor(Bean.class, RESOURCES_DIR, "*.xml", listener);
        listener.reset();
        Thread.sleep(1000);
        System.gc();
        assertListenerIsCalledForANewFile();
    }

    @Test
    public void testWatcherForASingleFile() throws Exception {
        watchFor(Bean.class, RESOURCES_DIR, BEAN_XML_NAME, listener);
        listener.reset();
        Thread.sleep(1000);
        String newValue = "another " + getActualValue();
        setActualValue(newValue);
        assertListenerIsCalledWith(newValue);
    }

    private void assertListenerIsCalledWith(String value) {
        assertThat(listener, should(beCalledWith(value))
                .whileWaitingUntil(timeoutHasExpired(60000)));
        listener.reset();
    }

    private void assertListenerIsCalledForANewFile() {
        assertListenerIsCalledWith(createNewFile());
    }

    private String createNewFile() {
        UUID uuid = UUID.randomUUID();
        String newXmlFileName = "bean-" + uuid + ".xml";
        File newFile = new File(RESOURCES_DIR + newXmlFileName);
        newFile.deleteOnExit();
        String value = "value-" + uuid;
        setActualValue(value, newFile);
        return value;
    }
}
