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
package xyz.jwizard.jwl.graph.neo4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import xyz.jwizard.jwl.common.bootstrap.CriticalBootstrapException;
import xyz.jwizard.jwl.graph.neo4j.client.factory.Neo4jConfig;
import xyz.jwizard.jwl.net.HostPort;

class Neo4jConfigUnitTest {
    @Test
    @DisplayName("should build valid Neo4jConfig when all required properties are provided")
    void shouldBuildValidConfig() {
        // when
        final Neo4jConfig config = Neo4jConfig.builder()
            .protocol(Neo4jGraphProtocol.NEO4J_S)
            .address(HostPort.from("localhost", 7687))
            .username("admin")
            .password("pass")
            .build();
        // then
        assertThat(config.getProtocol()).isEqualTo(Neo4jGraphProtocol.NEO4J_S);
        assertThat(config.getAddress().host()).isEqualTo("localhost");
        assertThat(config.getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("should throw CriticalBootstrapException when password is not provided")
    void shouldThrowExceptionWhenPasswordIsMissing() {
        // given
        final Neo4jConfig.Builder builder = Neo4jConfig.builder()
            .protocol(Neo4jGraphProtocol.BOLT)
            .address(HostPort.from("localhost", 7687))
            .username("admin");
        // when & then
        assertThatThrownBy(builder::build)
            .isInstanceOf(CriticalBootstrapException.class)
            .hasMessageContaining("Password cannot be null");
    }

    @Test
    @DisplayName("should throw CriticalBootstrapException when base protocol is missing")
    void shouldThrowExceptionWhenProtocolIsMissing() {
        // given
        final Neo4jConfig.Builder builder = Neo4jConfig.builder()
            .address(HostPort.from("localhost", 7687))
            .username("admin")
            .password("pass");
        // when & then
        assertThatThrownBy(builder::build)
            .isInstanceOf(CriticalBootstrapException.class)
            .hasMessageContaining("Protocol cannot be null");
    }
}
