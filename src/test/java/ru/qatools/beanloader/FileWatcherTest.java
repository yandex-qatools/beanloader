package ru.qatools.beanloader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.qatools.beanloader.internal.FileWatcher;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static ru.qatools.beanloader.BeanAssert.BEAN_XML_NAME;
import static ru.qatools.beanloader.BeanAssert.RESOURCES_DIR;
import static ru.qatools.beanloader.BeanAssert.getActualValue;
import static ru.qatools.beanloader.BeanAssert.setActualValue;
import static ru.qatools.beanloader.TestListener.beCalledWith;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.should;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.timeoutHasExpired;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileWatcherTest extends BeanChangingTest {

    private TestFileChangeListener listener;

    private ExecutorService executor;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        listener = new TestFileChangeListener();
        executor = newSingleThreadExecutor();
        executor.submit(new FileWatcher(Paths.get(RESOURCES_DIR), "glob:*.xml", listener));
        Thread.sleep(1000);
    }

    @Test
    public void testListenerIsNotCalledImmediately() {
        assertTrue(listener.isNotCalled());
    }

    @Test
    public void testListenerInvocationOnFileUpdate() throws Exception {
        setActualValue("another " + getActualValue());
        assertThat(listener, should(beCalledWith(RESOURCES_DIR + BEAN_XML_NAME))
                .whileWaitingUntil(timeoutHasExpired(60000)));
    }

    @Test
    public void testListenerInvocationOnNewFileCreationAndDeletion() throws Exception {
        String newXmlFileName = "another-bean.xml";
        File newFile = new File(RESOURCES_DIR + newXmlFileName);
        newFile.deleteOnExit();
        assertTrue(newFile.createNewFile());
        assertThat(listener, should(beCalledWith(newFile.getPath()))
                .whileWaitingUntil(timeoutHasExpired(60000)));
        listener.reset();
        assertTrue(newFile.delete());
        assertThat(listener, should(beCalledWith(newFile.getPath()))
                .whileWaitingUntil(timeoutHasExpired(60000)));
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        executor.shutdownNow();
    }
}
