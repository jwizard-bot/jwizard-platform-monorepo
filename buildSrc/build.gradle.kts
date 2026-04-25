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
    alias(libs.plugins.idea)
    alias(libs.plugins.java.gradle.plugin)
    alias(libs.plugins.kotlin.dsl)
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.shadow) apply false
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.gradle.node.plugin)
    implementation(libs.protobuf.gradle.plugin)
    implementation(libs.shadow.marker)
    implementation(libs.protoc)
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        create("jwServicePlugin") {
            id = "xyz.jwizard.jw-service"
            implementationClass = "xyz.jwizard.buildconfig.JwServicePlugin"
        }
    }
    plugins {
        create("jwPolyglotJs") {
            id = "xyz.jwizard.jw-polyglot-js"
            implementationClass = "xyz.jwizard.buildconfig.JwPolyglotJsPlugin"
        }
    }
    plugins {
        create("jwProtobuf") {
            id = "xyz.jwizard.jw-protobuf"
            implementationClass = "xyz.jwizard.buildconfig.JwProtobufPlugin"
        }
    }
}

idea {
    module {
        excludeDirs.add(file(".kotlin"))
    }
}
