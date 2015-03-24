# BeanLoader

[![release](http://github-release-version.herokuapp.com/github/yandex-qatools/beanloader/release.svg?style=flat)](https://github.com/yandex-qatools/beanloader/releases/latest) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.yandex.qatools.beanloader/beanloader/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.yandex.qatools.beanloader/beanloader)

This small library provides an easy fluent API to load xml beans
from different sources via JAXB, giving some of them higher priority
than to the others.

### Maven

```xml
<dependency>
    <groupId>ru.yandex.qatools.beanloader</groupId>
    <artifactId>beanloader</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Usage

```java
import static ru.qatools.beanloader.BeanLoaderStrategies.*;
import static ru.qatools.beanloader.BeanLoader.*;

BeanLoader<Bean> beanLoader = load(Bean.class)
        .from(resource("bean.xml"))
        .from(url("http://example.com?get-my-bean-dawg"))
        .from(file("~/beans/bean.xml"))
        .from(fileWithWatcher("/etc/beans/", "bean.xml"));

// load bean iterating over the given strategies
// until one of the returns a non-null bean
Bean bean = beanLoader.getBean();
makeSomeStuff(bean);

// reload the bean, if reloads are specified for any strategy
// returns the same object if no reloads are specified
bean = beanLoader.getBean();
makeAnotherStuff(bean);

```
