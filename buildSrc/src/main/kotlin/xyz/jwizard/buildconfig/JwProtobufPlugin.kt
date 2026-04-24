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

import com.google.protobuf.gradle.ProtobufExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.ide.idea.model.IdeaModel

class JwProtobufPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val protobufPlugin = target.libs.getPlugin("protobuf")

        target.pluginManager.apply(getPluginId(protobufPlugin))
        target.pluginManager.apply("idea")

        val protocLib = target.libs.getLibrary("protoc").get()
        val protocGroup = protocLib.module.group
        val protocName = protocLib.module.name
        val protocVersion = protocLib.versionConstraint.requiredVersion

        val protobufExt = target.extensions.getByType(ProtobufExtension::class.java)
        protobufExt.protoc {
            artifact = "$protocGroup:$protocName:$protocVersion"
        }
        configureSourceSets(target)
    }

    private fun configureSourceSets(project: Project) {
        val sourceSets = project.extensions.getByType<SourceSetContainer>()
        sourceSets.all {
            val generatedDir = project.file("build/generated/source/proto/$name/java")
            java.srcDir(generatedDir)
        }
        project.plugins.withId("idea") {
            val idea = project.extensions.getByType<IdeaModel>()
            with(idea.module) {
                val mainProto = project.file("src/main/proto")
                if (mainProto.exists()) {
                    sourceDirs.add(mainProto)
                }
                val testProto = project.file("src/test/proto")
                if (testProto.exists()) {
                    testSources.from(testProto)
                }
                val fixturesProto = project.file("src/testFixtures/proto")
                if (fixturesProto.exists()) {
                    testSources.from(fixturesProto)
                }
            }
        }
    }
}
