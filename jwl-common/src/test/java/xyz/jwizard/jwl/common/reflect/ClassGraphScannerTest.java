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
package xyz.jwizard.jwl.common.reflect;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClassGraphScannerTest {
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
