dependencies {
    implementation(libs.jedis)
    implementation(project(":jwl-common"))

    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.jupyter)
    testImplementation(testFixtures(project(":jwl-common")))
}
