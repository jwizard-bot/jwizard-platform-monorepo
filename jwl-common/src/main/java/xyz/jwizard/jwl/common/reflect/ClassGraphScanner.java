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
package xyz.jwizard.jwl.common.reflect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.common.util.io.IoUtil;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

public class ClassGraphScanner implements ClassScanner {
    private static final Logger LOG = LoggerFactory.getLogger(ClassGraphScanner.class);
    private final ScanResult scanResult;

    public ClassGraphScanner(String... packages) {
        LOG.info("Initializing class scanner for package(s): {}", Arrays.asList(packages));
        scanResult = new ClassGraph()
            .enableAnnotationInfo()
            .acceptPackages(packages)
            // enable all info about classes (fields, methods, etc.)
            .enableAllInfo()
            .scan();
    }

    @Override
    public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return new HashSet<>(scanResult
            .getClassesWithAnnotation(annotation.getName())
            .loadClasses());
    }

    @Override
    public <T> Set<Class<? extends T>> getSubtypesOf(Class<T> type) {
        return new HashSet<>(getRawSubtypes(type).loadClasses(type));
    }

    @Override
    public <T> Set<Class<? extends T>> getInstantiableSubtypesOf(Class<T> type) {
        final ClassInfoList concreteClasses = getRawSubtypes(type)
            .filter(info -> !info.isAbstract() && !info.isInterface());
        return new HashSet<>(concreteClasses.loadClasses(type));
    }

    @Override
    public void close() {
        IoUtil.closeQuietly(scanResult);
    }

    private ClassInfoList getRawSubtypes(Class<?> type) {
        if (type.isInterface()) {
            return scanResult.getClassesImplementing(type.getName());
        } else {
            return scanResult.getSubclasses(type.getName());
        }
    }
}
