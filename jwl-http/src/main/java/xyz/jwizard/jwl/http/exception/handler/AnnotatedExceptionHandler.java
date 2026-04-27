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
package xyz.jwizard.jwl.http.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.annotation.ResponseStatus;
import xyz.jwizard.jwl.net.http.HttpStatus;

public class AnnotatedExceptionHandler implements ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedExceptionHandler.class);

    @Override
    public boolean supports(Throwable throwable) {
        return throwable.getClass().isAnnotationPresent(ResponseStatus.class);
    }

    @Override
    public void handle(HttpRequest req, HttpResponse res, Throwable throwable) {
        final HttpStatus status = throwable.getClass().getAnnotation(ResponseStatus.class).value();
        LOG.warn("Annotated exception [{}]: {} -> Status {}",
            req.getPath(), throwable.getClass().getSimpleName(), status);
        res.setStatus(status);
        res.end();
    }
}
