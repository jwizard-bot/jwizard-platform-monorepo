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
package xyz.jwizard.jwl.kv.jedis.pubsub.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.jwizard.jwl.kv.pubsub.pattern.ChannelParamExtractor;

public class RegexChannelParamExtractor implements ChannelParamExtractor {
    private static final String[] EMPTY_PARAMS = new String[0];

    private final Pattern compiledPattern;

    public RegexChannelParamExtractor(String redisPattern) {
        if (redisPattern != null && redisPattern.contains("*")) {
            final String regexString = "\\Q" + redisPattern.replace("*", "\\E(.*)\\Q") + "\\E";
            this.compiledPattern = Pattern.compile(regexString);
        } else {
            this.compiledPattern = null;
        }
    }

    @Override
    public String[] extract(String channel) {
        if (compiledPattern == null || channel == null) {
            return EMPTY_PARAMS;
        }
        final Matcher matcher = compiledPattern.matcher(channel);
        if (matcher.matches()) {
            final int groupCount = matcher.groupCount();
            final String[] params = new String[groupCount];
            for (int i = 0; i < groupCount; i++) {
                params[i] = matcher.group(i + 1);
            }
            return params;
        }
        return EMPTY_PARAMS;
    }
}
