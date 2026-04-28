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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.net.http.header.CommonHttpHeaderName;

public class UserAgentInterceptor implements RequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(UserAgentInterceptor.class);

    private final String userAgent;

    public UserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public void intercept(InterceptorContext context) {
        LOG.trace("Applying user-agent: {}", userAgent);
        context.addUnsafeHeader(CommonHttpHeaderName.USER_AGENT, userAgent);
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE - 10;
    }
}
