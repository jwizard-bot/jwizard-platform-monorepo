dependencies {
    implementation(libs.ampq.client)
    implementation(project(":jwl-common"))

    testImplementation(testFixtures(project(":jwl-common")))
}
