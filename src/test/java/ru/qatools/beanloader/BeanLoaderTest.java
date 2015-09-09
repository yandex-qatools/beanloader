package ru.qatools.beanloader;

import org.junit.Test;

import java.io.File;

import static ru.qatools.beanloader.BeanAssert.BEAN_XML_NAME;
import static ru.qatools.beanloader.BeanAssert.BEAN_XML_PATH;
import static ru.qatools.beanloader.BeanAssert.RESOURCES_DIR;
import static ru.qatools.beanloader.BeanAssert.setActualValue;
import static ru.qatools.beanloader.BeanLoader.load;
import static ru.qatools.beanloader.BeanLoaderStrategies.file;
import static ru.qatools.beanloader.BeanLoaderStrategies.resource;
import static ru.qatools.beanloader.BeanLoaderStrategies.url;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class BeanLoaderTest {

    @Test
    public void testLoadFromTheFirstStrategy() {
        BeanAssert assertBean = new BeanAssert(load(Bean.class)
                .from(resource(BEAN_XML_NAME))
                .from(file("non existing file")));
        assertBean.valueIsEqualToActual();
        assertBean.valueIsEqualToActual();
    }

    @Test
    public void testLoadFromTheSecondStrategy() {
        BeanAssert assertBean = new BeanAssert(load(Bean.class)
                .from(resource("non existing resource"))
                .from(file(BEAN_XML_PATH)));
        assertBean.valueIsEqualToActual();
        assertBean.valueIsEqualToActual();
    }

    @Test
    public void testLoadWhenAllStrategiesFail() throws Exception {
        BeanAssert assertBean = new BeanAssert(load(Bean.class)
                .from(url("file:non existing file"))
                .from(resource("non existing resource"))
                .from(file("non existing file")));
        assertBean.isNull();
        assertBean.isNull();
    }

    @Test
    public void testReloadFromDifferentStrategy() {
        File anotherFile = new File(RESOURCES_DIR + "bean1.xml");

        BeanAssert assertBean = new BeanAssert(load(Bean.class)
                .from(file(anotherFile, true))
                .from(resource(BEAN_XML_NAME)));
        assertBean.valueIsEqualToActual();
        assertBean.valueIsEqualToActual();

        String anotherValue = "another value";
        setActualValue(anotherValue, anotherFile);
        anotherFile.deleteOnExit();

        assertBean.valueIsEqualTo(anotherValue);
        assertBean.valueIsEqualTo(anotherValue);
    }

    /**
     * To check that {@link NullPointerException} is not thrown
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testNullBeanClass() {
        load(null).from(resource(BEAN_XML_PATH)).getBean();
    }
}
