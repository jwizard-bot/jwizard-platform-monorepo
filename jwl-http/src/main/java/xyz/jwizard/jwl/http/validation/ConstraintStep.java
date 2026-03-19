package xyz.jwizard.jwl.http.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class ConstraintStep implements ValidationStep {
    private final Field field;
    private final Annotation annotation;
    private final AnnotationValidator<Annotation> validator;

    @SuppressWarnings("unchecked")
    ConstraintStep(Field field, Annotation annotation, AnnotationValidator<?> validator) {
        this.field = field;
        this.annotation = annotation;
        this.validator = (AnnotationValidator<Annotation>) validator;
    }

    @Override
    public void execute(Object target) {
        try {
            Object value = field.get(target);
            validator.validate(annotation, field, value);
        } catch (IllegalAccessException ignored) {
        }
    }
}
