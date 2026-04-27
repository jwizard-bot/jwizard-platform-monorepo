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
package xyz.jwizard.jwl.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NetworkUtilTest {
    @Test
    @DisplayName("should parse valid host:port string into HostPort object")
    void shouldParseValidAddressCorrectly() {
        // given
        final String address = "localhost:8080";
        // when
        final HostPort result = NetworkUtil.parseHostPort(address);
        // then
        assertNotNull(result, "Parsed HostPort object should not be null");
        assertEquals("localhost", result.host(), "Parsed host should match the input");
        assertEquals(8080, result.port(), "Parsed port should match the input");
    }

    @Test
    @DisplayName("should trim whitespaces and parse valid host:port string")
    void shouldTrimWhitespacesAndParseValidAddress() {
        // given
        final String address = "  127.0.0.1  :  9090  ";
        // when
        final HostPort result = NetworkUtil.parseHostPort(address);
        // then
        assertNotNull(result, "Parsed HostPort object should not be null");
        assertEquals("127.0.0.1", result.host(), "Host should be correctly trimmed");
        assertEquals(9090, result.port(), "Port should be correctly parsed after trimming");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when address is null")
    void shouldThrowExceptionWhenAddressIsNull() {
        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> NetworkUtil.parseHostPort(null),
            "Should throw IllegalArgumentException for null address");
        assertEquals("Address string cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when address is blank")
    void shouldThrowExceptionWhenAddressIsBlank() {
        // given
        final String blankAddress = "   ";
        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> NetworkUtil.parseHostPort(blankAddress),
            "Should throw IllegalArgumentException for blank address");
        assertEquals("Address string cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when address is missing a colon")
    void shouldThrowExceptionWhenAddressIsMissingColon() {
        // given
        final String address = "localhost8080";
        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> NetworkUtil.parseHostPort(address),
            "Should throw exception when format does not contain a colon");
        assertTrue(exception.getMessage().contains("Invalid address format"),
            "Exception message should indicate invalid format");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when address has multiple colons")
    void shouldThrowExceptionWhenAddressHasTooManyColons() {
        // given
        final String address = "localhost:8080:9090";
        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> NetworkUtil.parseHostPort(address),
            "Should throw exception when format contains more than one colon");
        assertTrue(exception.getMessage().contains("Invalid address format"),
            "Exception message should indicate invalid format");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when port is not a valid integer")
    void shouldThrowExceptionWhenPortIsNotANumber() {
        // given
        final String address = "localhost:abc";
        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> NetworkUtil.parseHostPort(address),
            "Should throw exception when port part cannot be parsed to an integer");
        assertTrue(exception.getMessage().contains("Invalid port number"),
            "Exception message should indicate invalid port number");
        assertNotNull(exception.getCause(), "Exception should contain the original cause");
        assertEquals(NumberFormatException.class, exception.getCause().getClass(),
            "Root cause should be NumberFormatException");
    }
}
