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
package xyz.jwizard.jwl.netclient.rest.intercept;

import xyz.jwizard.jwl.netclient.rest.TestHttpHeaderName;
import xyz.jwizard.jwl.netclient.rest.TestHttpHeaderValue;

public class CorrelationInterceptor implements RequestInterceptor {
    private final String correlationId;
    private final String correlationCode;

    public CorrelationInterceptor(String correlationId, String correlationCode) {
        this.correlationId = correlationId;
        this.correlationCode = correlationCode;
    }

    @Override
    public void intercept(InterceptorContext context) {
        context.addHeader(TestHttpHeaderName.X_CORRELATION_ID, TestHttpHeaderValue.REQ,
            correlationId, correlationCode);
        context.addQueryParam("tracking_enabled", "true");
    }

    @Override
    public int order() {
        return 10;
    }
}
