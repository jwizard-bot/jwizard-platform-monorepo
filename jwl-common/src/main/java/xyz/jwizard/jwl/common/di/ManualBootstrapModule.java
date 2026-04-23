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
package xyz.jwizard.jwl.common.di;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

import xyz.jwizard.jwl.common.util.CastUtil;

class ManualBootstrapModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(ManualBootstrapModule.class);

    private final Map<Class<?>, Class<?>> components;
    private final Map<Class<?>, Object> instanceComponents;

    ManualBootstrapModule(Map<Class<?>, Class<?>> components,
                          Map<Class<?>, Object> instanceComponents) {
        this.components = components;
        this.instanceComponents = instanceComponents;
    }

    @Override
    protected void configure() {
        LOG.debug("Configuring bootstrap module");
        for (Map.Entry<Class<?>, Class<?>> entry : components.entrySet()) {
            final Class<Object> interfaceClass = CastUtil.unsafeCast(entry.getKey());
            final Class<Object> implementationClass = CastUtil.unsafeCast(entry.getValue());
            LOG.debug("Binding infrastructure class: {} -> {}",
                interfaceClass.getSimpleName(), implementationClass.getSimpleName());
            bind(interfaceClass).to(implementationClass).asEagerSingleton();
        }
        for (Map.Entry<Class<?>, Object> entry : instanceComponents.entrySet()) {
            final Class<Object> type = CastUtil.unsafeCast(entry.getKey());
            final Object instance = entry.getValue();
            LOG.debug("Binding existing instance: {} -> instance of {}",
                type.getSimpleName(), instance.getClass().getSimpleName());
            bind(type).toInstance(instance);
        }
    }
}
