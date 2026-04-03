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
import xyz.jwizard.jwl.http.exception.RouteValidationException;
import xyz.jwizard.jwl.http.route.MatchResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ArgumentResolver {
    boolean supports(Parameter parameter);

    // runs per request, avoid O(N) operations
    Object resolve(Parameter parameter, HttpRequest req, MatchResult match) throws Exception;

    // runs once, at application startup
    default void validate(Method method) throws RouteValidationException {
    }
}
