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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import xyz.jwizard.jwl.common.bootstrap.ForbiddenInstantiationException;

public class CodecUtil {
    private CodecUtil() {
        throw new ForbiddenInstantiationException(CodecUtil.class);
    }

    public static String encodeBase64(String text) {
        if (text == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeBase64(String base64Text) {
        if (base64Text == null) {
            return null;
        }
        final byte[] decodedBytes = Base64.getDecoder().decode(base64Text);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
