package xyz.jwizard.jwl.common.reflect;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        ClassInfoList classes;
        if (type.isInterface()) {
            classes = scanResult.getClassesImplementing(type.getName());
        } else {
            classes = scanResult.getSubclasses(type.getName());
        }
        return new HashSet<>(classes.loadClasses(type));
    }

    @Override
    public void close() {
        if (scanResult != null) {
            scanResult.close();
        }
    }
}
