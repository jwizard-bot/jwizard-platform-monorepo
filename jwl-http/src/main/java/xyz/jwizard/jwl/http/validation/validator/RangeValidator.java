package xyz.jwizard.jwl.http.validation.validator;

import xyz.jwizard.jwl.http.annotation.Validator;
import xyz.jwizard.jwl.http.annotation.validation.Range;
import xyz.jwizard.jwl.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.http.validation.ValidationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Validator
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
            final String msg = annotation.message()
                .replace("{min}", String.valueOf(annotation.min()))
                .replace("{max}", String.valueOf(annotation.max()));
            throw new ValidationException(String.format("Field '%s' %s", field.getName(), msg));
        }
    }
}
