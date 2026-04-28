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
package xyz.jwizard.jwl.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CollectionUtilTest {
    @Test
    @DisplayName("should merge and consume two sorted lists in correct order")
    void shouldMergeSortedLists() {
        // given
        final List<Integer> list1 = List.of(1, 3, 5);
        final List<Integer> list2 = List.of(2, 4, 6);
        final List<Integer> result = new ArrayList<>();
        final Comparator<Integer> comparator = Integer::compareTo;
        // when
        CollectionUtil.consumeMergedSorted(list1, list2, comparator, item -> {
            result.add(item);
            return true;
        });
        // then
        assertThat(result).containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    @DisplayName("should handle lists of different sizes")
    void shouldHandleDifferentSizes() {
        // given
        final List<Integer> list1 = List.of(1, 10);
        final List<Integer> list2 = List.of(2, 3, 4, 5);
        final List<Integer> result = new ArrayList<>();
        // when
        CollectionUtil.consumeMergedSorted(list1, list2, Integer::compareTo, item -> {
            result.add(item);
            return true;
        });
        // then
        assertThat(result).containsExactly(1, 2, 3, 4, 5, 10);
    }

    @Test
    @DisplayName("should stop consuming when predicate returns false")
    void shouldAbortWhenPredicateReturnsFalse() {
        // given
        final List<Integer> list1 = List.of(1, 10, 20);
        final List<Integer> list2 = List.of(5, 15, 25);
        final List<Integer> result = new ArrayList<>();
        // when
        CollectionUtil.consumeMergedSorted(list1, list2, Integer::compareTo, item -> {
            if (item >= 15) {
                return false;
            }
            result.add(item);
            return true;
        });
        // then
        assertThat(result).containsExactly(1, 5, 10);
        assertThat(result).doesNotContain(15, 20, 25);
    }

    @Test
    @DisplayName("should handle one empty list")
    void shouldHandleOneEmptyList() {
        // given
        final List<Integer> list1 = List.of(1, 2, 3);
        final List<Integer> list2 = List.of();
        final List<Integer> result = new ArrayList<>();
        // when
        CollectionUtil.consumeMergedSorted(list1, list2, Integer::compareTo, item -> {
            result.add(item);
            return true;
        });
        // then
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("should do nothing when both lists are empty")
    void shouldDoNothingForBothEmpty() {
        // given
        final List<Integer> list1 = List.of();
        final List<Integer> list2 = List.of();
        final List<Integer> result = new ArrayList<>();
        // when
        CollectionUtil.consumeMergedSorted(list1, list2, Integer::compareTo, item -> {
            result.add(item);
            return true;
        });
        // then
        assertThat(result).isEmpty();
    }
}
