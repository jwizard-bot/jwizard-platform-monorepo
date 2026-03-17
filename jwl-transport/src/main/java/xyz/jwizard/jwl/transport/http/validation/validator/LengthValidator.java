package xyz.jwizard.jwl.transport.http.validation.validator;

import xyz.jwizard.jwl.transport.http.annotation.validation.Length;
import xyz.jwizard.jwl.transport.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.transport.http.validation.ValidationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class LengthValidator implements AnnotationValidator<Length> {
    @Override
    public boolean supports(Class<? extends Annotation> annotationType) {
        return Length.class.equals(annotationType);
    }

    @Override
    public void validate(Length annotation, Field field, Object value) {
        if (value == null) {
            return;
        }
        if (!(value instanceof String str)) {
            return;
        }
        final int len = str.length();
        if (len < annotation.min() || len > annotation.max()) {
            throw new ValidationException(String.format("Field '%s' length must be between %d and %d",
                field.getName(), annotation.min(), annotation.max()));
        }
    }
}
