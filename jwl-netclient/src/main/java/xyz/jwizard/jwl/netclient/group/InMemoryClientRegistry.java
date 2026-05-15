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
package xyz.jwizard.jwl.netclient.group;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryClientRegistry<T extends ClientGroupConfig> implements ClientRegistry<T> {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryClientRegistry.class);

    private final Map<ClientGroup, T> configs = new HashMap<>();

    private InMemoryClientRegistry() {
    }

    public static <T extends ClientGroupConfig> ClientRegistry<T> createDefault() {
        return new InMemoryClientRegistry<>();
    }

    @Override
    public void register(ClientGroup clientGroup, T config) {
        final String clientGroupName = clientGroup.getClientGroupName();
        if (configs.containsKey(clientGroup)) {
            LOG.warn("Overwriting existing configuration for client group: {}", clientGroupName);
        } else {
            LOG.info("Registering client group: {} -> {}", clientGroupName, config.getUrl());
        }
        configs.put(clientGroup, config);
    }

    @Override
    public void register(T config) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering configuration under default GLOBAL client group");
        }
        register(ClientGroup.GLOBAL, config);
    }

    @Override
    public T getConfig(ClientGroup clientGroup) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Fetching configuration for client group: {}",
                clientGroup.getClientGroupName());
        }
        final T groupConfig = configs.get(clientGroup);
        if (groupConfig == null) {
            throw new IllegalStateException("Requested config for unknown client group: " +
                clientGroup.getClientGroupName());
        }
        return groupConfig;
    }

    @Override
    public Map<ClientGroup, T> getConfigs() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Retrieving all registered client group configurations (total: {})",
                configs.size());
        }
        return configs;
    }
}
