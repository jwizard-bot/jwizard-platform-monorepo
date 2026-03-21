package xyz.jwizard.jwl.common.di;

import jakarta.inject.Singleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import xyz.jwizard.jwl.common.reflect.ClassScanner;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyInjectionTest {
    private ComponentProvider provider;

    @BeforeEach
    void setUp() {
        // given
        final ClassScanner scanner = Mockito.mock(ClassScanner.class);
        Mockito.when(scanner.getTypesAnnotatedWith(Singleton.class))
            .thenReturn(Set.of(MarkedComponent.class, SimpleComponent.class));
        final ApplicationContext context = new ApplicationContext(scanner);
        provider = context.getProvider();
    }

    @Test
    @DisplayName("should provide singleton instances for all @Injectable classes")
    void shouldProvideSingletonInjectables() {
        // when
        final MarkedComponent instance1 = provider.getInstance(MarkedComponent.class);
        final MarkedComponent instance2 = provider.getInstance(MarkedComponent.class);
        final SimpleComponent simple = provider.getInstance(SimpleComponent.class);
        // then
        assertThat(instance1).isNotNull();
        assertThat(simple).isNotNull();
        assertThat(instance1).isSameAs(instance2); // singleton test
    }

    @Test
    @DisplayName("should find instances by custom annotation")
    void shouldFindInstancesByAnnotation() {
        // when
        Collection<Object> found = provider.getInstancesAnnotatedWith(TestMarker.class);
        // then
        assertThat(found).hasSize(1);
        assertThat(found.iterator().next()).isInstanceOf(MarkedComponent.class);
    }

    @Test
    @DisplayName("should return empty collection when no components have the annotation")
    void shouldReturnEmptyForMissingAnnotation() {
        // when
        final Collection<Object> found = provider.getInstancesAnnotatedWith(Override.class);
        // then
        assertThat(found).isEmpty();
    }
}

@Singleton
@TestMarker
class MarkedComponent {
}

@Singleton
class SimpleComponent {
}
