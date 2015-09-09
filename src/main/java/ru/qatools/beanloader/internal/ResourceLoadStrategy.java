package ru.qatools.beanloader.internal;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public class ResourceLoadStrategy extends UrlLoadStrategy {

    private final String resource;

    public ResourceLoadStrategy(String resource) {
        super(ResourceLoadStrategy.class.getClassLoader().getResource(resource), false);
        this.resource = resource;
    }

    @Override
    protected String getSourceDescription() {
        return "classpath resource " + resource;
    }
}
