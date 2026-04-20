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
package xyz.jwizard.jwl.kv.jedis.pubsub;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import xyz.jwizard.jwl.kv.pubsub.KvChannel;
import xyz.jwizard.jwl.kv.pubsub.TestKvChannel;
import xyz.jwizard.jwl.kv.pubsub.subscriber.AbstractKvSubscriber;
import xyz.jwizard.jwl.kv.pubsub.subscriber.SubscriptionMode;

import jakarta.inject.Singleton;

@Singleton
public class PatternStringTestSubscriber extends AbstractKvSubscriber<String> {
    private CountDownLatch latch;
    private AtomicReference<String> receivedRef;
    private AtomicReference<String[]> paramsRef;

    public void prepareForTest(CountDownLatch latch,
                               AtomicReference<String> receivedRef,
                               AtomicReference<String[]> paramsRef) {
        this.latch = latch;
        this.receivedRef = receivedRef;
        this.paramsRef = paramsRef;
    }

    @Override
    public KvChannel getChannel() {
        return TestKvChannel.USER_EVENTS_WILDCARD;
    }

    @Override
    public Class<String> getPayloadType() {
        return String.class;
    }

    @Override
    public SubscriptionMode getMode() {
        return SubscriptionMode.PATTERN;
    }

    @Override
    public void handle(String channel, String[] params, String message) {
        if (paramsRef != null) {
            paramsRef.set(params);
        }
        if (receivedRef != null) {
            receivedRef.set(message);
        }
        if (latch != null) {
            latch.countDown();
        }
    }
}
