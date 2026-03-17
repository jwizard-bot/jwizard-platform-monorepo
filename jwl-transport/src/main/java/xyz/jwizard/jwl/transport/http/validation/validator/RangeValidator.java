package xyz.jwizard.jwl.transport.http.validation.validator;

import xyz.jwizard.jwl.transport.http.annotation.validation.Range;
import xyz.jwizard.jwl.transport.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.transport.http.validation.ValidationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class RangeValidator implements AnnotationValidator<Range> {
    @Override
    public boolean supports(Class<? extends Annotation> annotationType) {
        return Range.class.equals(annotationType);
    }

    @Override
    public void validate(Range annotation, Field field, Object value) {
        if (value == null) {
            return;
        }
        if (!(value instanceof Number num)) {
            return;
        }
        final double doubleVal = num.doubleValue();
        if (doubleVal < annotation.min() || doubleVal > annotation.max()) {
            final String errorMessage = String.format("Field '%s' must be between %d and %d",
                field.getName(), annotation.min(), annotation.max());
            throw new ValidationException(errorMessage);
        }
    }
}
