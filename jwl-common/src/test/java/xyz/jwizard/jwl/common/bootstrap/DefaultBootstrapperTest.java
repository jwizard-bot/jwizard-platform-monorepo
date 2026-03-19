package xyz.jwizard.jwl.common.bootstrap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import xyz.jwizard.jwl.common.di.ComponentProvider;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.mockito.Mockito.*;

public class DefaultBootstrapperTest {
    @Test
    @DisplayName("should start hooks in correct priority order")
    void shouldStartHooksInOrder() {
        // given
        final ComponentProvider mockProvider = mock(ComponentProvider.class);
        final LifecycleHook highPriority = mock(LifecycleHook.class);
        final LifecycleHook lowPriority = mock(LifecycleHook.class);

        when(highPriority.priority()).thenReturn(100);
        when(lowPriority.priority()).thenReturn(1);

        final List<LifecycleHook> hooks = Arrays.asList(lowPriority, highPriority);
        // when
        final List<? extends LifecycleHook> sortedHooks = hooks.stream()
            .sorted(Comparator.comparingInt(LifecycleHook::priority).reversed())
            .toList();
        sortedHooks.forEach(hook -> hook.onStart(mockProvider));
        // then
        final InOrder inOrder = inOrder(highPriority, lowPriority);
        inOrder.verify(highPriority).onStart(mockProvider); // first: 100
        inOrder.verify(lowPriority).onStart(mockProvider);  // after: 1
    }
}
