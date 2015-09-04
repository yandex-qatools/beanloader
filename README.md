# BeanLoader

[![release](http://github-release-version.herokuapp.com/github/yandex-qatools/beanloader/release.svg?style=flat)](https://github.com/yandex-qatools/beanloader/releases/latest) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.yandex.qatools.beanloader/beanloader/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.yandex.qatools.beanloader/beanloader)
[![covarage](https://img.shields.io/sonar/http/sonar.qatools.ru/ru.yandex.qatools.beanloader:beanloader/coverage.svg?style=flat)](http://sonar.qatools.ru/dashboard/index/547)

This small library with no dependencies provides an easy fluent API to load xml beans
from different sources via JAXB, giving some of them higher priority
than to the others.

BeanLoader is useful if you are facing one of the following cases:

1. You need to load a bean from a range of locations and stop, when you load the bean successfully. For example, when you need to load a bean from a file, but in case the file is missing you've got a classpath resource to use instead. Or another file etc.
2. You need to reload the bean multiple times from the same resource. Of course, you can just write a ```fetchAndGetMyBean()``` and call it over and over again, but why?
3. You want to use a ```java.nio.file.WatchService``` to load the bean every time it is changed externally.

### Maven

```xml
<dependency>
    <groupId>ru.yandex.qatools.beanloader</groupId>
    <artifactId>beanloader</artifactId>
    <version>1.0</version>
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

### Examples

#### 1) Basic unmarshalling
This code
```java
Bean bean = (Bean) JAXB.unmarshal(this.getClass().getClassLoader().getResource("bean.xml"), Bean.class);
```
is equivalent to:
```java
Bean bean = load(Bean.class).from(resource("bean.xml")).getBean();
```
And this
```java
Bean bean = (Bean) JAXB.unmarshal(new File("/etc/bean.xml"), Bean.class);
```
to this:
```java
Bean bean = load(Bean.class).from(file("/etc/bean.xml")).getBean();
```

#### 2) Reloading a file every time
Lets say you want to reload fresh aata for your bean every time some your method is called. Of course you could just instert the load-n-unmarshal code right into your method but it may not be convenient for multiple reasons. With BeanLoader you can rganize your code as follows:
```java
public class MyClass {

    private final BeanLoader<Bean> beanLoader;

    public MyClass(String filename) {
        this.beanLoader = load(Bean.class).from(file(filename, true));
    }
    
    public void doSomeStuff() {
        // do some other stuff
        Bean bean = beanLoader.getBean();
        // do some stuff with the bean
    }
}
```
The second boolean parameter here indicates that a file will be reloaded on every call to ```beanLoader.getBean()``` method.

#### 3) Using a file watcher
Now suppose realoading a file every time doesn't suit your needs and you want to use a ```java.nio.file.WatchService``` to change the loaded bean only when it gets changed externally. Instead of implementing your own thread and ```while(true)``` loop you can just do it in a couple of lines:
```java
public class MyClass {

    private final BeanLoader<Bean> beanLoader;

    public MyClass(String filename, String directory) {
        this.beanLoader = load(Bean.class).from(fileWithWatcher(directory, filename));
    }

    public void doSomeStuff() {
        Bean bean = beanLoader.getBean();
        // do some stuff with the bean
    }
}
```
And that's it! The eban will be reloaded only when it is changed and you'll get the fresh version of your bean on every call to ```beanLoader.getBean()``` guaranteed. Although remember that not every platform supports watching files.

#### 4) Using a file watcher with a listener
Sometimes you need do something with the bean immediately when it changes. For example, log it's contents or fire some message. This can be achieved with the help of ```BeanChangeListener``` interface. See the following code:
```java
public class MyClass implements BeanChangeListener<Bean> {

    public MyClass(String filename, String directory) {
        load(Bean.class).from(fileWithWatcher(directory, filename, this))
                        .getBean();
    }

    @Override
    public void beanChanged(Bean newBean) {
        System.out.println("Wow, new bean is here! Take a look: " + stringify(newBean));
    }
}
```
Notice, you may not even need to declasre the beanLoader instance, if all you want is to be notified of changes. Notice how ```getBean()``` is called right after ```beanLoader``` creation to fire the initial bean change.
