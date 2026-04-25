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

plugins {
    alias(libs.plugins.test.fixtures)
}

dependencies {
    implementation(libs.bucket4j)
    implementation(libs.clazz.graph)
    implementation(libs.guava)
    implementation(libs.guice) {
        // 7.0.0 has vulnerable old guava version, fetch the newest version explicitly
        exclude(group = "com.google.guava", module = "guava")
    }

    api(libs.jakarta.inject.api)
    api(libs.jakarta.cdi.api)

    testFixturesApi(libs.assertj.core)
    testFixturesApi(libs.junit.jupiter)
    testFixturesApi(libs.slf4j.api)
}
