plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(libs.jackson.databind)
    implementation(libs.clazz.graph)

    testFixturesApi(libs.slf4j.api)
    testFixturesApi(libs.assertj.core)
    testFixturesApi(libs.junit.jupiter)
}
