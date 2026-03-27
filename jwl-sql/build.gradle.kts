dependencies {
    implementation(libs.hikari.cp)
    implementation(project(":jwl-common"))

    testImplementation(libs.postgresql)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.jupyter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(testFixtures(project(":jwl-common")))
}
