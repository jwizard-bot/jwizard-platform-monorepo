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
package xyz.jwizard.jwl.http.resolver.body;

import java.io.IOException;
import java.io.InputStream;

import org.jspecify.annotations.NonNull;

import xyz.jwizard.jwl.http.exception.RequestTooLargeException;

class LimitedInputStream extends InputStream {
    private final InputStream delegate;
    private final long limit;
    private long bytesRead = 0;

    LimitedInputStream(InputStream delegate, long limit) {
        this.delegate = delegate;
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        int b = delegate.read();
        if (b != -1) {
            checkLimit(1);
        }
        return b;
    }

    @Override
    public int read(byte @NonNull [] b, int off, int len) throws IOException {
        int count = delegate.read(b, off, len);
        if (count > 0) {
            checkLimit(count);
        }
        return count;
    }

    private void checkLimit(int count) {
        bytesRead += count;
        if (bytesRead > limit) {
            throw new RequestTooLargeException(String
                .format("Actual bytes read so far: %d, max allowed: %d bytes", bytesRead, limit)
            );
        }
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
