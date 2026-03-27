package xyz.jwizard.jwl.http.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.cache.ProviderCache;
import xyz.jwizard.jwl.http.annotation.validation.NestedValid;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ValidationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ValidationHandler.class);

    private final Map<Class<?>, List<ValidationStep>> classPlanCache = new ConcurrentHashMap<>();
    private final ProviderCache<Class<? extends Annotation>,
        Class<? extends Annotation>, AnnotationValidator<?>> validatorCache;

    public ValidationHandler(Set<AnnotationValidator<?>> validators) {
        validatorCache = new ProviderCache<>(validators, AnnotationValidator::supports);
        LOG.info("Initialized ValidatorHandler with {} validator(s)", validators.size());
    }

    public void validate(Object target) {
        if (target == null) {
            return;
        }
        final Class<?> clazz = target.getClass();
        final List<ValidationStep> steps = classPlanCache.computeIfAbsent(clazz,
            this::buildValidationPlan);
        // isDebugEnabled for prevent string concatenation overhead when debug is disabled
        if (!steps.isEmpty()) {
            LOG.debug("Executing {} validation step(s) for object of class: {}", steps.size(),
                clazz.getSimpleName());
        }
        for (final ValidationStep step : steps) {
            step.execute(target);
        }
    }

    private List<ValidationStep> buildValidationPlan(Class<?> clazz) {
        LOG.debug("Cache miss, building validation plan for class: {}", clazz.getName());
        final List<ValidationStep> steps = new ArrayList<>();
        for (final Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(NestedValid.class)) {
                LOG.debug("Added nested validation step for field: {}", field.getName());
                steps.add(new NestedStep(field, this));
            }
            for (final Annotation annotation : field.getAnnotations()) {
                final Class<? extends Annotation> annType = annotation.annotationType();
                final AnnotationValidator<?> validator = validatorCache.get(annType, annType);
                if (validator != null) {
                    LOG.debug("Mapped field '{}' to validator: {}", field.getName(),
                        validator.getClass().getSimpleName());
                    steps.add(new ConstraintStep(field, annotation, validator));
                }
            }
        }
        LOG.debug("Validation plan built successfully, cached {} step(s) for class: {}",
            steps.size(), clazz.getSimpleName());
        return steps;
    }
}
