package dev.minceraft.sonus.common.natives;
// Created by booky10 in Sonus (20:45 20.12.2025)

import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Encapsulates loading natives to prevent classname conflicts
 * and properly unload native libraries when sonus gets shut down.
 */
@NullMarked
public class NativesLoader implements AutoCloseable {

    private static final String PREFIX = "META-INF/sonus-natives/";

    private final URLClassLoader classLoader;

    NativesLoader(String name) {
        try {
            Path resourcePath = externalizeResource(name);
            URL resourceUrl = resourcePath.toUri().toURL();
            this.classLoader = new URLClassLoader(new URL[]{resourceUrl});
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }

    // java doesn't support creating classloaders for nested JARs and fails silently...
    // please just tell me the error instead of hiding it deep in classloading internals
    private static Path externalizeResource(String name) {
        ClassLoader loader = NativesLoader.class.getClassLoader();
        try (InputStream resource = loader.getResourceAsStream(PREFIX + name)) {
            if (resource == null) {
                throw new IllegalArgumentException("Can't find resource with name " + name);
            }
            Path tempPath = Files.createTempFile("sonus-natives_", "_" + name);
            Files.copy(resource, tempPath, REPLACE_EXISTING);
            return tempPath;
        } catch (IOException exception) {
            throw new RuntimeException("Error while copying " + name + " to external location", exception);
        }
    }

    public Class<?> loadClass(String name) {
        try {
            return this.classLoader.loadClass(name);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public URLClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public void close() {
        try {
            this.classLoader.close();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
