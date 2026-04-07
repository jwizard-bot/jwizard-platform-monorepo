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
package xyz.jwizard.buildconfig

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure

abstract class JwPolyglotJsExtension {
    abstract val nodeVersion: Property<String>
    abstract val entryPoints: MapProperty<String, String> // output name <-> entrypoint location
    abstract val npmDependencies: ListProperty<String> // additional node packages to install

    init {
        nodeVersion.convention("24.14.1")
        npmDependencies.convention(listOf("esbuild"))
    }
}

fun Project.jwPolyglotJs(action: JwPolyglotJsExtension.() -> Unit) {
    configure<JwPolyglotJsExtension>(action)
}
