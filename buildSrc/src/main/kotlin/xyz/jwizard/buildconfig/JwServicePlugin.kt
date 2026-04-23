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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import kotlin.reflect.KProperty1

class JwServicePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("jwService", JwServiceExtension::class.java)
        applyApplicationConventions(target, extension)
        target.afterEvaluate {
            extension.require(JwServiceExtension::packageSuffix, target.name)
            extension.require(JwServiceExtension::mainClass, target.name)
        }
    }

    private fun applyApplicationConventions(project: Project, jwExt: JwServiceExtension) {
        project.pluginManager.apply(project.libs.getPlugin("shadow").get().pluginId)
        project.pluginManager.apply("application")
        project.dependencies {
            add("runtimeOnly", project.libs.getLibrary("logback.classic"))
        }
        val mainClazzProvider = jwExt.packageSuffix.zip(jwExt.mainClass) { suffix, clazz ->
            "${project.group}.jws.$suffix.$clazz"
        }
        project.configure<JavaApplication> {
            mainClass.set(mainClazzProvider)
        }
        project.tasks.withType<ShadowJar>().configureEach {
            archiveFileName.set("${project.name}.jar")
            destinationDirectory.set(project.layout.projectDirectory.dir(".bin"))
            manifest {
                attributes("Main-Class" to mainClazzProvider.get())
            }
        }
    }

    private fun JwServiceExtension.require(
        propertyRef: KProperty1<JwServiceExtension, Property<*>>,
        projectName: String,
    ) {
        if (!propertyRef.get(this).isPresent) {
            throw GradleException(
                "Error in '$projectName': Missing required value '${propertyRef.name}' in " +
                    "jwizard { } block."
            )
        }
    }
}
