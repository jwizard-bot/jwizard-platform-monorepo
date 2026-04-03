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

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.annotation.RequestParam;
import xyz.jwizard.jwl.http.route.MatchResult;

import java.lang.reflect.Parameter;

public class RequestParamResolver implements ArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpRequest req, MatchResult match) {
        final RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        final String paramName = annotation.value();

        String paramValue = req.getQueryParam(paramName);
        if (paramValue == null) {
            if (annotation.defaultValue().isEmpty()) {
                if (annotation.required()) {
                    throw new IllegalArgumentException("Required request parameter '" + paramName
                        + "' is missing");
                }
                return null;
            }
            paramValue = annotation.defaultValue();
        }
        return ParameterConverter.parse(parameter.getType(), paramValue);
    }
}
