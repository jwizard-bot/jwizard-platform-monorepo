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
package xyz.jwizard.jwl.netclient.rest.jetty.spec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import xyz.jwizard.jwl.netclient.rest.spec.GenericRequestSpec;

class JettyBodyStrategyTest {
    private final JettyBodyStrategy rawStrategy = new JettyRawBodyStrategy();
    private final JettyBodyStrategy formStrategy = new JettyFormBodyStrategy();

    @Test
    @DisplayName("raw strategy should support requests with raw body and no form params")
    void rawStrategyShouldSupportRawBody() {
        // given
        final GenericRequestSpec spec = mock(GenericRequestSpec.class);
        when(spec.getBody()).thenReturn("Some Raw JSON");
        when(spec.getFormParams()).thenReturn(null);
        // when & then
        assertThat(rawStrategy.supports(spec)).isTrue();
        assertThat(formStrategy.supports(spec)).isFalse();
    }

    @Test
    @DisplayName("form strategy should support requests with form params")
    void formStrategyShouldSupportFormParams() {
        // given
        final GenericRequestSpec spec = mock(GenericRequestSpec.class);
        when(spec.getBody()).thenReturn(null);
        when(spec.getFormParams()).thenReturn(Map.of("grant_type", "client_credentials"));
        // when & then
        assertThat(formStrategy.supports(spec)).isTrue();
        assertThat(rawStrategy.supports(spec)).isFalse();
    }
}
