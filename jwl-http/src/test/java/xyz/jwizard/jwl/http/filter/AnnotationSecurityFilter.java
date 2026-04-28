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
package xyz.jwizard.jwl.http.filter;

import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.TestConstants;
import xyz.jwizard.jwl.http.header.TestHttpHeaderName;
import xyz.jwizard.jwl.http.header.TestHttpHeaderValue;
import xyz.jwizard.jwl.net.http.HttpStatus;
import xyz.jwizard.jwl.net.http.header.CommonHttpHeaderName;

import jakarta.inject.Singleton;

@Singleton
public class AnnotationSecurityFilter extends SecureRouteFilter {
    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res) {
        final String token = req.getHeader(CommonHttpHeaderName.AUTHORIZATION);
        if (!TestConstants.TEST_PASSWORD.equals(token)) {
            res.setStatus(HttpStatus.UNAUTHORIZED_401);
            return false;
        }
        res.setHeader(TestHttpHeaderName.X_SECURED_BY, TestHttpHeaderValue.ANNOTATION_FILTER);
        return true;
    }

    @Override
    public int order() {
        return 1000;
    }
}

