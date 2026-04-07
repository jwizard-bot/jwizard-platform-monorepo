/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.jwizard.jwl.common.util.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.function.Predicate;

public class IoUtil {
    private static final Logger LOG = LoggerFactory.getLogger(IoUtil.class);

    private IoUtil() {
    }

    public static void thrownQuietly(RunnableWithException runnableWithException) {
        if (runnableWithException == null) {
            return;
        }
        try {
            runnableWithException.run();
        } catch (Exception ex) {
            LOG.error("Cloud not perform action, cause: {}", ex.getMessage(), ex);
        }
    }

    public static <T> void closeQuietly(T resource, CloseAction<T> closeAction) {
        if (resource == null || closeAction == null) {
            return;
        }
        try {
            closeAction.perform(resource);
        } catch (Exception ex) {
            LOG.warn("Failed to close resource safely, cause: {}", ex.getMessage(), ex);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        closeQuietly(closeable, AutoCloseable::close);
    }

    public static void closeQuietly(Runnable runnable) {
        closeQuietly(runnable, Runnable::run);
    }

    public static <T> void closeQuietly(T resource, Predicate<T> predicate,
                                        CloseAction<T> closeAction) {
        closeQuietly(resource, r -> {
            if (predicate.test(r)) {
                closeAction.perform(r);
            }
        });
    }

    public static String removeTrailingSlash(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    // works safety between different modules without flat fat-jar structure
    public static URL getRequiredResourceUrl(String rawPath) throws IOException {
        final String path = removeTrailingSlash(rawPath);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL resourceUrl = classLoader.getResource(path);
        if (resourceUrl == null) {
            throw new IOException("Unable to find file on classpath: " + rawPath);
        }
        return resourceUrl;
    }
}
