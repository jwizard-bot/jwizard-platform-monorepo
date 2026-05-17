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

import xyz.jwizard.jwl.common.registry.GenericConcurrentRegistry;

public class InMemoryClientRegistry<T extends ClientGroupConfig>
    extends GenericConcurrentRegistry<ClientGroup, T> implements ClientRegistry<T> {
    private InMemoryClientRegistry() {
        super();
    }

    public static <T extends ClientGroupConfig> ClientRegistry<T> createDefault() {
        return new InMemoryClientRegistry<>();
    }

    @Override
    public void register(ClientGroup clientGroup, T config) {
        super.register(clientGroup, config);
    }

    @Override
    public void register(T config) {
        if (log.isDebugEnabled()) {
            log.debug("Registering configuration under default GLOBAL client group");
        }
        super.register(ClientGroup.GLOBAL, config);
    }
}
