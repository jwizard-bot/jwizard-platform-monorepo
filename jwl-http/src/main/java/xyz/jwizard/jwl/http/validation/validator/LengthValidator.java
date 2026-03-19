package xyz.jwizard.jwl.http.validation.validator;

import xyz.jwizard.jwl.http.annotation.validation.Length;
import xyz.jwizard.jwl.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.http.validation.ValidationException;

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
            final String msg = annotation.message()
                .replace("{min}", String.valueOf(annotation.min()))
                .replace("{max}", String.valueOf(annotation.max()));
            throw new ValidationException(String.format("Field '%s' %s", field.getName(), msg));
        }
    }
}
