package ru.qatools.beanloader;

import org.junit.Test;

import java.io.File;

import static ru.qatools.beanloader.BeanAssert.*;
import static ru.qatools.beanloader.BeanLoader.load;
import static ru.qatools.beanloader.BeanLoaderStrategies.*;

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
}
