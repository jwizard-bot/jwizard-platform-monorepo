plugins {
    `java-test-fixtures`
}

dependencies {
    testFixturesApi(libs.slf4j.api)
    testFixturesApi(libs.assertj.core)
    testFixturesApi(libs.junit.jupiter)
}
