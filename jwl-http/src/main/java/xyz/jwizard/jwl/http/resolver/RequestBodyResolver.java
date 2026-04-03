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
package xyz.jwizard.jwl.http.resolver;

import xyz.jwizard.jwl.common.serialization.json.JsonSerializer;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.annotation.Body;
import xyz.jwizard.jwl.http.exception.RouteValidationException;
import xyz.jwizard.jwl.http.route.MatchResult;
import xyz.jwizard.jwl.http.validation.ValidationHandler;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class RequestBodyResolver implements ArgumentResolver {
    private final JsonSerializer jsonSerializer;
    private final ValidationHandler validationHandler;

    public RequestBodyResolver(JsonSerializer jsonSerializer, ValidationHandler validationHandler) {
        this.jsonSerializer = jsonSerializer;
        this.validationHandler = validationHandler;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(Body.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpRequest req, MatchResult match) throws Exception {
        if (req.getLength() == 0) {
            return null;
        }
        try (final InputStream in = req.getInputStream()) {
            final Object body = jsonSerializer.deserialize(in, parameter.getType());
            if (body != null) {
                validationHandler.validate(body);
            }
            return body;
        }
    }

    @Override
    public void validate(Method method) throws RouteValidationException {
        final long bodyParams = Arrays.stream(method.getParameters())
            .filter(this::supports)
            .count();
        if (bodyParams > 1) {
            throw new RouteValidationException(method,
                "Multiple @Body parameters detected, only one request body is allowed per route");
        }
    }
}
