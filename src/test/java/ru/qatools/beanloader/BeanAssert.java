package ru.qatools.beanloader;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import ru.qatools.beanloader.internal.BeanLoadStrategy;

import java.io.File;

import static javax.xml.bind.JAXB.marshal;
import static javax.xml.bind.JAXB.unmarshal;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static ru.qatools.beanloader.BeanLoader.load;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.should;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.timeoutHasExpired;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class BeanAssert {

    public static final String RESOURCES_DIR = "src/test/resources/";
    public static final String BEAN_XML_NAME = "bean.xml";
    public static final String BEAN_XML_PATH = RESOURCES_DIR + BEAN_XML_NAME;
    public static final String BEAN_XML_URL = "file:" + BEAN_XML_PATH;
    public static final File BEAN_XML_FILE = new File(BEAN_XML_PATH);

    private final String actualValue = getActualValue();

    private final BeanLoader<Bean> loader;

    public BeanAssert(BeanLoadStrategy strategy) {
        this(load(Bean.class).from(strategy));
    }

    public BeanAssert(BeanLoader<Bean> beanLoader) {
        loader = beanLoader;
    }

    public static String getActualValue() {
        return unmarshal(BEAN_XML_FILE, Bean.class).getValue();
    }

    public static void setActualValue(String value) {
        setActualValue(value, BEAN_XML_FILE);
    }

    public static void setActualValue(String value, File file) {
        Bean bean = new Bean();
        bean.setValue(value);
        marshal(bean, file);
    }

    public void isNull() {
        assertThat(loader.getBean(), is(nullValue()));
    }

    public void valueIsEqualTo(String value) {
        assertThat(loader.getBean().getValue(), equalTo(value));
    }

    public void valueIsEqualToActual() {
        valueIsEqualTo(actualValue);
    }

    public void valueHasNotChanged() {
        valueIsEqualToActual();
    }

    public void waitUntilValueIsEqualTo(String value) {
        assertThat(loader, should(haveBeanWith(value))
                .whileWaitingUntil(timeoutHasExpired(60000)));
    }

    private Matcher<BeanLoader<Bean>> haveBeanWith(final String value) {
        return new TypeSafeMatcher<BeanLoader<Bean>>() {

            @Override
            protected boolean matchesSafely(BeanLoader<Bean> item) {
                return item.getBean().getValue().equals(value);
            }

            @Override
            protected void describeMismatchSafely(BeanLoader<Bean> item, Description description) {
                description.appendText("bean with value = " + item.getBean().getValue());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("bean with value = " + value);
            }
        };
    }
}
