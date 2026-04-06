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

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.common.util.CastUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

class AutoScanModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(AutoScanModule.class);

    private final ClassScanner classScanner;

    AutoScanModule(ClassScanner classScanner) {
        this.classScanner = classScanner;
    }

    @Override
    protected void configure() {
        LOG.debug("Auto-configuring bindings for @Injectable component(s)");
        bind(ComponentProvider.class).to(GuiceComponentProvider.class).in(Scopes.SINGLETON);

        final Provider<Injector> injectorProvider = getProvider(Injector.class);
        final Set<Class<?>> injectables = classScanner.getTypesAnnotatedWith(Singleton.class);
        int boundedComponents = 0;
        for (final Class<?> clazz : injectables) {
            if (isInstantiableClass(clazz)) {
                LOG.debug("Binding component: {}", clazz.getName());
                bind(clazz).in(Scopes.SINGLETON);
                registerBeans(clazz, injectorProvider);
                boundedComponents++;
            }
        }
        LOG.info("Successfully bound {} component(s)", boundedComponents);
    }

    private void registerBeans(Class<?> clazz, Provider<Injector> injectorProvider) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Produces.class)) {
                continue;
            }
            final Class<Object> returnType = CastUtil.unsafeCast(method.getReturnType());
            if (returnType.equals(void.class)) {
                throw new IllegalStateException("@Produces method cannot return void: " +
                    method.getName());
            }
            bind(returnType)
                .toProvider(new BeanProvider<>(clazz, method, injectorProvider))
                .in(Scopes.SINGLETON);
            LOG.debug("Bound @Produces method: {} from {}.{}()",
                returnType.getSimpleName(), clazz.getSimpleName(), method.getName());
        }
    }

    private boolean isInstantiableClass(Class<?> clazz) {
        return !clazz.isInterface()
            && !clazz.isAnnotation()
            && !clazz.isEnum()
            && !Modifier.isAbstract(clazz.getModifiers());
    }
}
