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
package xyz.jwizard.jwl.http.resolver.body;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import xyz.jwizard.jwl.codec.serialization.MessageSerializer;
import xyz.jwizard.jwl.codec.serialization.SerializerRegistry;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.annotation.Body;
import xyz.jwizard.jwl.http.exception.RequestTooLargeException;
import xyz.jwizard.jwl.http.exception.RouteValidationException;
import xyz.jwizard.jwl.http.resolver.ArgumentResolver;
import xyz.jwizard.jwl.http.route.MatchResult;
import xyz.jwizard.jwl.http.validation.ValidationHandler;

public class RequestBodyResolver implements ArgumentResolver {
    private final SerializerRegistry<MessageSerializer> serializerRegistry;
    private final ValidationHandler validationHandler;

    public RequestBodyResolver(SerializerRegistry<MessageSerializer> serializerRegistry,
                               ValidationHandler validationHandler) {
        this.serializerRegistry = serializerRegistry;
        this.validationHandler = validationHandler;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(Body.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpRequest req, MatchResult match)
        throws Exception {
        final long contentLength = req.getLength();
        if (contentLength == 0) {
            return null;
        }
        final BodyMediaSerializer mapping = BodyMediaSerializer
            .resolve(parameter.getType(), req.getContentType());

        final Body annotation = parameter.getAnnotation(Body.class);
        final long limit = (annotation != null && annotation.limit() > 0)
            ? annotation.unit().toBytes(annotation.limit())
            : mapping.getMaxSizeBytes();
        if (contentLength > limit) {
            throw new RequestTooLargeException(String
                .format("Declared Content-Length: %d bytes, max allowed: %d bytes", contentLength,
                    limit)
            );
        }
        final MessageSerializer serializer = serializerRegistry.get(mapping.getFormat());
        if (serializer == null) {
            throw new IllegalStateException("No serializer registered for format: " +
                mapping.getFormat());
        }
        try (final InputStream in = new LimitedInputStream(req.getInputStream(), limit)) {
            final Object body = serializer.deserializeFromStream(in, parameter.getType());
            if (body != null && mapping.isValidate()) {
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
