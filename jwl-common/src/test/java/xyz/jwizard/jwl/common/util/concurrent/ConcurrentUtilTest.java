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
package xyz.jwizard.jwl.common.util.concurrent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class ConcurrentUtilTest {
    @Test
    @DisplayName("should complete successfully when onSuccess is invoked")
    void shouldCompleteSuccessfullyWhenOnSuccessInvoked() {
        // expect: no exception is thrown
        assertDoesNotThrow(() ->
            ConcurrentUtil.await(IoCallback::onSuccess)
        );
    }

    @Test
    @DisplayName("should rethrow RuntimeException unwrapped")
    void shouldRethrowRuntimeExceptionUnwrapped() {
        // given
        final RuntimeException expectedException = new IllegalArgumentException("Invalid state");
        // when & then
        final IllegalArgumentException actualException =
            assertThrows(IllegalArgumentException.class, () ->
                ConcurrentUtil.await(callback -> callback.onFailure(expectedException))
            );
        assertEquals("Invalid state", actualException.getMessage());
    }

    @Test
    @DisplayName("should wrap checked exception in ConcurrentOperationException")
    void shouldWrapCheckedExceptionInConcurrentOperationException() {
        // given
        final Exception checkedException = new IOException("Disk failure");
        // when & then
        final ConcurrentOperationException actualException =
            assertThrows(ConcurrentOperationException.class, () ->
                ConcurrentUtil.await(callback -> callback.onFailure(checkedException))
            );
        assertEquals(checkedException, actualException.getCause());
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    @DisplayName("should block thread until callback is invoked")
    void shouldBlockThreadUntilCallbackIsInvoked() {
        // given
        final long startTime = System.currentTimeMillis();
        final long sleepTimeMs = 100;
        // when
        ConcurrentUtil.await(callback -> {
            // async I/O operation in separated thread
            Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(sleepTimeMs);
                    callback.onSuccess();
                } catch (InterruptedException e) {
                    callback.onFailure(e);
                }
            });
        });
        // then
        final long executionTime = System.currentTimeMillis() - startTime;
        assertTrue(executionTime >= sleepTimeMs,
            "Method should block for at least " + sleepTimeMs + "ms, but took " + executionTime
                + "ms");
    }
}
