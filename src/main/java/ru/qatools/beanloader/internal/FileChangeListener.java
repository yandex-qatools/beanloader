package ru.qatools.beanloader.internal;

import java.nio.file.Path;

/**
 * @author Innokenty Shuvalov innokenty@yandex-team.ru
 */
public interface FileChangeListener {

    void fileChanged(Path path);
}
