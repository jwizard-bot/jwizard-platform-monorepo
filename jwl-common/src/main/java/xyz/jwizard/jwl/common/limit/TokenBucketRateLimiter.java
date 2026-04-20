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
package xyz.jwizard.jwl.common.limit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

public class TokenBucketRateLimiter implements RateLimiter {
    private static final Logger LOG = LoggerFactory.getLogger(TokenBucketRateLimiter.class);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Supplier<Bucket> bucketSupplier;

    private TokenBucketRateLimiter(Builder builder) {
        final Bandwidth limit = Bandwidth.builder()
            .capacity(builder.capacity)
            .refillGreedy(builder.refillTokens, builder.refillPeriod)
            .build();
        bucketSupplier = () -> Bucket.builder()
            .addLimit(limit)
            .build();
        LOG.debug("Initialized TokenBucketRateLimiter with capacity: {}, refill tokens: {}, " +
                "refill period: {}ms", builder.capacity, builder.refillTokens,
            builder.refillPeriod.toMillis());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean tryAcquire(String key) {
        final Bucket bucket = buckets.computeIfAbsent(key, k -> bucketSupplier.get());
        return bucket.tryConsume(1);
    }

    @Override
    public void reset(String key) {
        buckets.remove(key);
    }

    public static class Builder {
        private long capacity;
        private long refillTokens;
        private Duration refillPeriod;

        private Builder() {
        }

        public Builder capacity(long capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder refillTokens(long refillTokens) {
            this.refillTokens = refillTokens;
            return this;
        }

        public Builder refillPeriod(Duration refillPeriod) {
            this.refillPeriod = refillPeriod;
            return this;
        }

        public RateLimiter build() {
            return new TokenBucketRateLimiter(this);
        }
    }
}
