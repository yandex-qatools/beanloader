package ru.qatools.beanloader;

import ru.qatools.beanloader.internal.FileChangeListener;

import java.nio.file.Path;

public class TestFileChangeListener extends TestListener implements FileChangeListener {

    @Override
    public void fileChanged(Path path) {
        update(path.toString());
    }
}
