/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.jwizard.jwl.http.validation.validator;

import xyz.jwizard.jwl.http.annotation.Validator;
import xyz.jwizard.jwl.http.annotation.validation.Length;
import xyz.jwizard.jwl.http.validation.AnnotationValidator;
import xyz.jwizard.jwl.http.validation.ValidationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Validator
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
