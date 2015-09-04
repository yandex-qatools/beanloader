package ru.qatools.beanloader;

import org.junit.Test;

import static ru.qatools.beanloader.BeanAssert.BEAN_XML_NAME;
import static ru.qatools.beanloader.BeanAssert.RESOURCES_DIR;
import static ru.qatools.beanloader.BeanAssert.getActualValue;
import static ru.qatools.beanloader.BeanAssert.setActualValue;
import static ru.qatools.beanloader.BeanLoaderStrategies.fileWithWatcher;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class FileWithWatcherLoadStrategyTest extends BeanChangingTest {

    @Test
    public void testFileWatcher() throws Exception {
        BeanAssert assertBean = new BeanAssert(fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME));

        assertBean.valueIsEqualToActual();
        Thread.sleep(1000);
        String newValue = "another " + getActualValue();
        setActualValue(newValue);
        assertBean.valueHasNotChanged();
        assertBean.waitUntilValueIsEqualTo(newValue);
    }
}
