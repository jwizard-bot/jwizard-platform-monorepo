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
package xyz.jwizard.jwl.net.http.auth;

import java.util.function.Function;

import xyz.jwizard.jwl.common.util.CodecUtil;

public enum StandardAuthScheme implements AuthScheme {
    BEARER("Bearer", 1, args -> args[0]),
    BASIC("Basic", 2, args -> CodecUtil.encodeBase64(args[0] + ":" + args[1]));

    private final String schemeName;
    private final int requiredParams;
    private final Function<String[], String> formatter;

    StandardAuthScheme(String schemeName, int requiredParams,
                       Function<String[], String> formatter) {
        this.schemeName = schemeName;
        this.requiredParams = requiredParams;
        this.formatter = formatter;
    }

    @Override
    public String buildHeaderValue(String... credentials) {
        final int providedCount = (credentials == null) ? 0 : credentials.length;
        if (providedCount != requiredParams) {
            throw new IllegalArgumentException(String.format(
                "%s auth requires exactly %d parameter(s), but got %d", schemeName, requiredParams,
                providedCount
            ));
        }
        final String formattedCredentials = formatter.apply(credentials);
        return schemeName + " " + formattedCredentials;
    }
}
