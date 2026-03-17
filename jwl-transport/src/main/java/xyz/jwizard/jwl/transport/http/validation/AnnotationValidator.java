package xyz.jwizard.jwl.transport.http.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface AnnotationValidator<A extends Annotation> {
    boolean supports(Class<? extends Annotation> annotationType);

    void validate(A annotation, Field field, Object value) throws ValidationException;
}
