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
package xyz.jwizard.jwl.common.util.math;

import xyz.jwizard.jwl.common.bootstrap.ForbiddenInstantiationException;

public class MathUtil {
    private MathUtil() {
        throw new ForbiddenInstantiationException(MathUtil.class);
    }

    public static long calcExpBackoff(int attempt, long baseDelayMs, long maxDelayMs) {
        if (baseDelayMs <= 0) {
            return 0;
        }
        final long expDelay = baseDelayMs * (long) Math.pow(2, Math.max(0, attempt - 1));
        final double jitterFactor = 1.0 + (Math.random() * 0.2 - 0.1);
        final long finalDelay = (long) (expDelay * jitterFactor);
        return Math.min(finalDelay, maxDelayMs);
    }
}
