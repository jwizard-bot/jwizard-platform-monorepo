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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import xyz.jwizard.jwl.common.bootstrap.ForbiddenInstantiationException;

public class StringUtil {
    private StringUtil() {
        throw new ForbiddenInstantiationException(StringUtil.class);
    }

    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(Locale.ROOT);
    }

    public static String toUpperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(Locale.ROOT);
    }

    // splits by char using fast indexOf() to avoid regex overhead, unpredictable trim patterns
    public static List<String> split(String str, char separator) {
        if (str == null) {
            return new ArrayList<>();
        }
        final List<String> result = new ArrayList<>();
        int start = 0;
        int nextSeparator;
        while ((nextSeparator = str.indexOf(separator, start)) != -1) {
            result.add(str.substring(start, nextSeparator));
            start = nextSeparator + 1;
        }
        result.add(str.substring(start));
        return result;
    }

    public static String splitAndGetFirst(String str, char separator) {
        final List<String> results = split(str, separator);
        if (results.isEmpty()) {
            return null;
        }
        return results.getFirst();
    }

    public static byte[] getBytes(String str) {
        if (str == null) {
            return new byte[0];
        }
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String truncateToUtf8Bytes(String text, int maxBytes) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        final byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) {
            return text;
        }
        final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.IGNORE)
            .onUnmappableCharacter(CodingErrorAction.IGNORE);
        try {
            final ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, maxBytes);
            final CharBuffer decoded = decoder.decode(buffer);
            return decoded.toString();
        } catch (CharacterCodingException ex) {
            return "";
        }
    }
}
