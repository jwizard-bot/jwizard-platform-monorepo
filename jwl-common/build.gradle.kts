plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(libs.jackson.databind)

    testFixturesApi(libs.slf4j.api)
    testFixturesApi(libs.assertj.core)
    testFixturesApi(libs.junit.jupiter)
}
