dependencies {
    implementation(libs.ampq.client)
    implementation(project(":jwl-common"))

    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.jupyter)
    testImplementation(libs.testcontainers.rabbitmq)
    testImplementation(testFixtures(project(":jwl-common")))
}
