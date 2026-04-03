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

import jakarta.inject.Singleton;
import xyz.jwizard.jwl.http.HttpRequest;
import xyz.jwizard.jwl.http.HttpResponse;
import xyz.jwizard.jwl.http.route.Route;

import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class CacheSpyFilter implements HttpFilter {
    public static final AtomicInteger supportsCounter = new AtomicInteger(0);

    @Override
    public boolean supports(Route route) {
        if ("/api/map".equals(route.path())) {
            supportsCounter.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res) {
        return true;
    }

    @Override
    public int order() {
        return 100;
    }
}
