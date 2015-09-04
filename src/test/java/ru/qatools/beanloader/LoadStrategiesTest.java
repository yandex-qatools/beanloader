package ru.qatools.beanloader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import ru.qatools.beanloader.internal.BeanLoadStrategy;
import ru.qatools.beanloader.internal.FileLoadStrategy;
import ru.qatools.beanloader.internal.FileWatcherLoadStrategy;
import ru.qatools.beanloader.internal.ResourceLoadStrategy;
import ru.qatools.beanloader.internal.UrlLoadStrategy;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;

import static ru.qatools.beanloader.BeanAssert.BEAN_XML_NAME;
import static ru.qatools.beanloader.BeanAssert.BEAN_XML_PATH;
import static ru.qatools.beanloader.BeanAssert.BEAN_XML_URL;
import static ru.qatools.beanloader.BeanAssert.RESOURCES_DIR;
import static ru.qatools.beanloader.BeanLoaderStrategies.file;
import static ru.qatools.beanloader.BeanLoaderStrategies.fileWithWatcher;
import static ru.qatools.beanloader.BeanLoaderStrategies.resource;
import static ru.qatools.beanloader.BeanLoaderStrategies.url;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
@RunWith(Parameterized.class)
public class LoadStrategiesTest {

    @Parameters(name = "{2}")
    public static Collection<Object[]> data() throws MalformedURLException {
        return Arrays.asList(new Object[][]{
                {
                        resource(BEAN_XML_NAME),
                        resource("non existing file"),
                        ResourceLoadStrategy.class.getSimpleName()
                },
                {
                        file(BEAN_XML_PATH),
                        file("non existing file"),
                        FileLoadStrategy.class.getSimpleName()
                },
                {
                        file(BEAN_XML_PATH, true),
                        file("non existing file", true),
                        FileLoadStrategy.class.getSimpleName() + " with reload"
                },
                {
                        fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME),
                        fileWithWatcher("non existing directory", "non existing file"),
                        FileWatcherLoadStrategy.class.getSimpleName()
                },
                {
                        url(BEAN_XML_URL),
                        url("file:non existing file"),
                        UrlLoadStrategy.class.getSimpleName()
                },
                {
                        url(BEAN_XML_URL, true),
                        url("file:non existing file", true),
                        UrlLoadStrategy.class.getSimpleName() + " with reload"
                },
        });
    }

    private final BeanAssert assertExistingBean;
    private final BeanAssert assertAbsentBean;

    public LoadStrategiesTest(BeanLoadStrategy existingBeanStrategy,
                              BeanLoadStrategy absentBeanStrategy,
                              @SuppressWarnings("UnusedParameters") String name) {
        assertExistingBean = new BeanAssert(existingBeanStrategy);
        assertAbsentBean = new BeanAssert(absentBeanStrategy);
    }

    @Test
    public void testWithExistingBean() {
        assertExistingBean.valueIsEqualToActual();
        assertExistingBean.valueIsEqualToActual();
    }

    @Test
    public void testWithNotExistingBean() {
        assertAbsentBean.isNull();
        assertAbsentBean.isNull();
    }
}
