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
package xyz.jwizard.jws.ingestor;

import java.util.List;

import xyz.jwizard.jwl.common.bootstrap.lifecycle.LifecycleHook;
import xyz.jwizard.jwl.common.di.ComponentProvider;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.graph.GraphReader;
import xyz.jwizard.jwl.graph.GraphServer;
import xyz.jwizard.jwl.graph.GraphWriter;
import xyz.jwizard.jwl.graph.client.GraphClient;
import xyz.jwizard.jwl.graph.neo4j.Neo4jGraphProtocol;
import xyz.jwizard.jwl.graph.neo4j.Neo4jServer;
import xyz.jwizard.jwl.graph.neo4j.client.factory.DefaultNeo4jClientFactory;
import xyz.jwizard.jwl.graph.neo4j.client.factory.Neo4jConfig;
import xyz.jwizard.jwl.graph.neo4j.repository.Neo4jGraphRepository;
import xyz.jwizard.jwl.net.HostPort;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
class GraphServerLifecycle implements LifecycleHook {
    private final GraphServer<Neo4jConfig> graphServer;

    GraphServerLifecycle() {
        graphServer = Neo4jServer.builder()
            .config(Neo4jConfig.builder()
                .protocol(Neo4jGraphProtocol.BOLT) /*TODO: incoming from config server*/
                .address(HostPort.from("localhost", 9118)) /*TODO: incoming from config server*/
                .username("neo4j") /*TODO: incoming from config server*/
                .password("root") /*TODO: incoming from config server*/
                .build()
            )
            .clientFactory(DefaultNeo4jClientFactory.create())
            .repositoryFactory(Neo4jGraphRepository::createDefault)
            .build();
    }

    @Override
    public void onStart(ComponentProvider componentProvider, ClassScanner scanner) {
        graphServer.start();
    }

    @Override
    public void onStop() {
        graphServer.close();
    }

    @Override
    public List<Class<? extends LifecycleHook>> dependsOn() {
        return List.of(JsEngineLifecycle.class);
    }

    @Produces
    @Singleton
    GraphReader graphReader() {
        return graphServer.getRepository();
    }

    @Produces
    @Singleton
    GraphWriter graphWriter() {
        return graphServer.getRepository();
    }

    @Produces
    @Singleton
    GraphClient graphClient() {
        return graphServer.getClient();
    }
}
