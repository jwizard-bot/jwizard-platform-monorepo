package xyz.jwizard.jwl.http.validation.validator;

import xyz.jwizard.jwl.http.annotation.Validator;
import xyz.jwizard.jwl.http.annotation.validation.NotNull;
import xyz.jwizard.jwl.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.http.validation.ValidationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Validator
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
