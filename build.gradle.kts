import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    alias(libs.plugins.shadow) apply false
}

allprojects {
    group = "xyz.jwizard"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    dependencies {
        implementation(rootProject.libs.slf4j.api)
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

fun getPluginId(accessor: Provider<PluginDependency>) = accessor.get().pluginId
