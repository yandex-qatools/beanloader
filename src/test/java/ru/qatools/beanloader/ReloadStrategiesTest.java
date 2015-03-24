package ru.qatools.beanloader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import ru.qatools.beanloader.internal.*;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;

import static ru.qatools.beanloader.BeanAssert.*;
import static ru.qatools.beanloader.BeanLoaderStrategies.*;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
@RunWith(Parameterized.class)
public class ReloadStrategiesTest extends BeanChangingTest {

    @Parameters(name = "{2}")
    public static Collection<Object[]> data() throws MalformedURLException {
        return Arrays.asList(new Object[][]{
                {
                        resource(BEAN_XML_NAME),
                        false,
                        ResourceLoadStrategy.class.getSimpleName()
                },
                {
                        file(BEAN_XML_PATH),
                        false,
                        FileLoadStrategy.class.getSimpleName()
                },
                {
                        file(BEAN_XML_PATH, true),
                        true,
                        FileLoadStrategy.class.getSimpleName() + " with reload"
                },
                {
                        fileWithWatcher(RESOURCES_DIR, BEAN_XML_NAME),
                        false,
                        FileWithWatcherLoadStrategy.class.getSimpleName()
                },
                {
                        url(BEAN_XML_URL),
                        false,
                        UrlLoadStrategy.class.getSimpleName()
                },
                {
                        url(BEAN_XML_URL, true),
                        true,
                        UrlLoadStrategy.class.getSimpleName() + " with reload"
                },
        });
    }

    private final BeanAssert assertBean;
    private final boolean mustUpdate;

    public ReloadStrategiesTest(BeanLoadStrategy existingBeanStrategy,
                                boolean mustUpdate,
                                @SuppressWarnings("UnusedParameters") String name) {
        this.mustUpdate = mustUpdate;
        this.assertBean = new BeanAssert(existingBeanStrategy);
    }

    @Test
    public void testBeanUpdates() {
        assertBean.valueIsEqualToActual();
        String newValue = "another " + getActualValue();
        setActualValue(newValue);
        if (mustUpdate) {
            assertBean.valueIsEqualTo(newValue);
            assertBean.valueIsEqualTo(newValue);
        } else {
            assertBean.valueHasNotChanged();
            assertBean.valueHasNotChanged();
        }
    }
}
