plugins {
    id("java-test-fixtures")
}

dependencies {
    implementation(libs.clazz.graph)
    implementation(libs.guava)
    implementation(libs.guice) {
        // 7.0.0 has vulnerable old guava version, fetch the newest version explicitly
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation(libs.jackson.databind)

    api(libs.jakarta.inject.api)
    api(libs.jakarta.cdi.api)

    testFixturesApi(libs.assertj.core)
    testFixturesApi(libs.junit.jupiter)
    testFixturesApi(libs.slf4j.api)
}
