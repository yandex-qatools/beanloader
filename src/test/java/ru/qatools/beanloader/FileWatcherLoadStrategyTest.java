package ru.qatools.beanloader;

import org.junit.Test;
import ru.qatools.beanloader.internal.BeanChangeListener;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static ru.qatools.beanloader.BeanAssert.BEAN_XML_NAME;
import static ru.qatools.beanloader.BeanAssert.RESOURCES_DIR;
import static ru.qatools.beanloader.BeanAssert.getActualValue;
import static ru.qatools.beanloader.BeanAssert.setActualValue;
import static ru.qatools.beanloader.BeanLoaderStrategies.fileWithWatcher;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileWatcherLoadStrategyTest extends BeanChangingTest {

    @Test
    public void testFileWatcher() throws Exception {
        TestBeanChangeListener listener = new TestBeanChangeListener();

        BeanAssert assertBean = new BeanAssert(fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME, listener));
        assertBean.valueIsEqualToActual();

        assertTrue(listener.isOnChangeMethodCalled());
        listener.reset();

        Thread.sleep(1000);
        String newValue = "another " + getActualValue();
        setActualValue(newValue);
        assertBean.valueHasNotChanged();

        assertFalse(listener.isOnChangeMethodCalled());

        assertBean.waitUntilValueIsEqualTo(newValue);
        assertTrue(listener.isOnChangeMethodCalled());
    }

    private class TestBeanChangeListener implements BeanChangeListener<Bean> {

        boolean onChangeMethodCalled;

        public boolean isOnChangeMethodCalled() {
            return onChangeMethodCalled;
        }

        public void reset() {
            onChangeMethodCalled = false;
        }

        @Override
        public void beanChanged(Bean newBean) {
            onChangeMethodCalled = true;
        }
    }
}
