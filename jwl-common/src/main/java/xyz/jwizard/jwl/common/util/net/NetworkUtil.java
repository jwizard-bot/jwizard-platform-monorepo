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
package xyz.jwizard.jwl.common.util.net;

public class NetworkUtil {
    private NetworkUtil() {
    }

    public static HostPort parseHostPort(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address string cannot be null or empty.");
        }
        final String[] parts = address.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid address format. Expected 'host:port', " +
                "but got: '" + address + "'");
        }
        try {
            final String host = parts[0].trim();
            final int port = Integer.parseInt(parts[1].trim());
            return new HostPort(host, port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number in address definition: '" +
                address + "'", e);
        }
    }
}
