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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension

fun ExtraPropertiesExtension.set(prop: ModuleProp, value: Any?) {
    this.set(prop.key, value)
}

inline fun <reified T> ExtraPropertiesExtension.require(prop: ModuleProp, project: Project): T {
    if (!this.has(prop.key)) {
        throw GradleException(
            "CONFIGURATION ERROR: Module [${project.name}] has missing required property " +
                "'${prop.key}' (ModuleProp.${prop.name})."
        )
    }
    val value = this.get(prop.key)
    if (value !is T) {
        throw GradleException(
            "TYPE ERROR: Property '${prop.key}' in module [${project.name}] " +
                "must be of type ${T::class.simpleName}, but found ${value?.javaClass?.simpleName}."
        )
    }
    return value
}
