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
import xyz.jwizard.buildconfig.JwPolyglotJsPlugin
import xyz.jwizard.buildconfig.JwServicePlugin
import xyz.jwizard.buildconfig.jwPolyglotJs
import xyz.jwizard.buildconfig.jwService

apply<JwServicePlugin>()
apply<JwPolyglotJsPlugin>()

jwService {
    packageSuffix.set("ingestor")
    mainClass.set("JwsIngestorMain")
}

jwPolyglotJs {
    entryPoints.put("yarn-parser.bundle", "node_modules/@yarnpkg/parsers/lib/index.js")
    npmDependencies.add("@yarnpkg/parsers")
}

dependencies {
    implementation(libs.graalvm.polyglot)
    implementation(libs.graalvm.polyglot.js)
    implementation(libs.gradle.tooling.api)
    implementation(project(":jwl-common"))
    implementation(project(":jwl-graph"))
    implementation(project(":jwl-http"))

    testImplementation(testFixtures(project(":jwl-common")))
}
