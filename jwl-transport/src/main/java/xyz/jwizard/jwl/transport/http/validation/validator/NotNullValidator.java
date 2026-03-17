package xyz.jwizard.jwl.transport.http.validation.validator;

import xyz.jwizard.jwl.transport.http.annotation.validation.NotNull;
import xyz.jwizard.jwl.transport.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.transport.http.validation.ValidationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class NotNullValidator implements AnnotationValidator<NotNull> {
    @Override
    public boolean supports(Class<? extends Annotation> annotationType) {
        return NotNull.class.equals(annotationType);
    }

    @Override
    public void validate(NotNull annotation, Field field, Object value) {
        if (value != null) {
            return;
        }
        throw new ValidationException("Field '" + field.getName() + "' " + annotation.message());
    }
}
