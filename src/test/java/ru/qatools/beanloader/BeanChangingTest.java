package ru.qatools.beanloader;

import org.junit.After;
import org.junit.Before;

import static ru.qatools.beanloader.BeanAssert.getActualValue;
import static ru.qatools.beanloader.BeanAssert.setActualValue;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public abstract class BeanChangingTest {

    private String actualValueAtTheBeginningOfTheTest;

    @Before
    public void setUp() throws Exception {
        actualValueAtTheBeginningOfTheTest = getActualValue();
    }

    @After
    public void tearDown() throws Exception {
        setActualValue(actualValueAtTheBeginningOfTheTest);
    }
}
