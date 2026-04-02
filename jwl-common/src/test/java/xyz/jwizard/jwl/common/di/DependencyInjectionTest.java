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

import jakarta.inject.Singleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.jwizard.jwl.common.reflect.ClassScanner;
import xyz.jwizard.jwl.common.reflect.TypeReference;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DependencyInjectionTest {
    private ComponentProvider componentProvider;

    @BeforeEach
    void setUp() {
        // given
        final ClassScanner scanner = mock(ClassScanner.class);
        when(scanner.getTypesAnnotatedWith(Singleton.class))
            .thenReturn(Set.of(
                MarkedComponent.class,
                SimpleComponent.class,
                TestInterfaceComponent.class,
                SecondTestInterfaceComponent.class
            ));
        final ApplicationContext context = new ApplicationContext(scanner);
        componentProvider = context.getComponentProvider();
    }

    @Test
    @DisplayName("should provide singleton instances for all @Injectable classes")
    void shouldProvideSingletonInjectables() {
        // when
        final MarkedComponent instance1 = componentProvider.getInstance(MarkedComponent.class);
        final MarkedComponent instance2 = componentProvider.getInstance(MarkedComponent.class);
        final SimpleComponent simple = componentProvider.getInstance(SimpleComponent.class);
        // then
        assertThat(instance1).isNotNull();
        assertThat(simple).isNotNull();
        assertThat(instance1).isSameAs(instance2); // singleton test
    }

    @Test
    @DisplayName("should find instances by custom annotation")
    void shouldFindInstancesByAnnotation() {
        // when
        Collection<Object> found = componentProvider.getInstancesAnnotatedWith(TestMarker.class);
        // then
        assertThat(found).hasSize(1);
        assertThat(found.iterator().next()).isInstanceOf(MarkedComponent.class);
    }

    @Test
    @DisplayName("should return empty collection when no components have the annotation")
    void shouldReturnEmptyForMissingAnnotation() {
        // when
        final Collection<Object> found = componentProvider.getInstancesAnnotatedWith(Override.class);
        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should find all instances by TypeReference")
    void shouldFindInstancesByTypeReference() {
        // given
        final TypeReference<TestInterface> pluginType = new TypeReference<>() {
        };
        // when
        final Collection<TestInterface> plugins = componentProvider.getInstancesOf(pluginType);
        // then
        assertThat(plugins)
            .hasSize(2)
            .hasOnlyElementsOfTypes(
                TestInterfaceComponent.class,
                SecondTestInterfaceComponent.class
            );
    }
}

@Singleton
@TestMarker
class MarkedComponent {
}

@Singleton
class SimpleComponent {
}

@Singleton
class TestInterfaceComponent implements TestInterface {
}

@Singleton
class SecondTestInterfaceComponent implements TestInterface {
}
