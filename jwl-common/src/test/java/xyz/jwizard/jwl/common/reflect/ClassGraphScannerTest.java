package xyz.jwizard.jwl.common.reflect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassGraphScannerTest {
    @Test
    @DisplayName("should find only classes annotated with @TestComponent")
    void shouldFindAnnotatedClasses() {
        // given
        final String currentPackage = this.getClass().getPackageName();
        // when
        try (ClassGraphScanner scanner = new ClassGraphScanner(currentPackage)) {
            final Set<Class<?>> foundClasses = scanner.getTypesAnnotatedWith(TestComponent.class);
            // then
            assertThat(foundClasses)
                .hasSizeGreaterThanOrEqualTo(2)
                .contains(ValidComponentOne.class, ValidComponentTwo.class)
                .doesNotContain(IgnoredComponent.class);
        }
    }

    @Test
    @DisplayName("should return an empty set when the package does not exist")
    void shouldReturnEmptySetForNonExistentPackage() {
        // given
        final String nonExistentPackage = "xyz.jwizard.jwl.this.package.does.not.exist";
        // when
        try (ClassGraphScanner scanner = new ClassGraphScanner(nonExistentPackage)) {
            final Set<Class<?>> foundClasses = scanner.getTypesAnnotatedWith(TestComponent.class);
            // then
            assertThat(foundClasses).isEmpty();
        }
    }
}

@TestComponent
class ValidComponentOne {
}

@TestComponent
class ValidComponentTwo {
}

// class without the annotation - should not be found
class IgnoredComponent {
}
