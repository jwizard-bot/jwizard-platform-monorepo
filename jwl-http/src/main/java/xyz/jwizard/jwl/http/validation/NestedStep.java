package xyz.jwizard.jwl.http.validation;

import java.lang.reflect.Field;

class NestedStep implements ValidationStep {
    private final Field field;
    private final ValidationHandler validationHandler;

    NestedStep(Field field, ValidationHandler validationHandler) {
        this.field = field;
        this.validationHandler = validationHandler;
    }

    @Override
    public void execute(Object target) {
        try {
            Object nestedObject = field.get(target);
            if (nestedObject != null) {
                validationHandler.validate(nestedObject);
            }
        } catch (IllegalAccessException ignored) {
        }
    }
}
