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
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    alias(libs.plugins.shadow) apply false
}

allprojects {
    group = "xyz.jwizard"
    version = getEnv("VERSION", "0.0.0")

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    dependencies {
        implementation(rootProject.libs.slf4j.api)
        testImplementation(rootProject.libs.assertj.core)
        testImplementation(rootProject.libs.junit.jupiter)
        testImplementation(rootProject.libs.mockito.core)
        testImplementation(rootProject.libs.mockito.jupiter)
        testRuntimeOnly(rootProject.libs.logback.classic)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            showStandardStreams = true
        }
        // suppress JDK 21+ warnings regarding dynamic agent loading (used by mockito)
        // -Xshare:off: disables class data sharing
        jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
    }

    if (name.startsWith("jws-")) {
        apply(plugin = getPluginId(rootProject.libs.plugins.shadow))

        dependencies {
            runtimeOnly(rootProject.libs.logback.classic)
        }

        tasks.withType<ShadowJar> {
            archiveFileName.set("${project.name}.jar")
            destinationDirectory.set(layout.projectDirectory.dir(".bin"))
        }
    }
}

fun getPluginId(accessor: Provider<PluginDependency>): String {
    return accessor.get().pluginId
}

fun getEnv(name: String, defValue: String = ""): String {
    return System.getenv("JW_$name") ?: defValue
}
