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
package xyz.jwizard.jwl.common.util;

import xyz.jwizard.jwl.common.bootstrap.ForbiddenInstantiationException;

public class PathUtil {
    private PathUtil() {
        throw new ForbiddenInstantiationException(PathUtil.class);
    }

    public static String combinePaths(String prefix, String path) {
        if (prefix == null || prefix.isBlank() || prefix.equals("/")) {
            return ensureLeadingSlash(path);
        }
        final String cleanPrefix = prefix.replaceAll("/+$", ""); // remove slash from the end
        final String cleanPath = path.replaceAll("^/+", ""); // remove slash from the beginning
        return ensureLeadingSlash(cleanPrefix + "/" + cleanPath);
    }

    public static String ensureLeadingSlash(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
