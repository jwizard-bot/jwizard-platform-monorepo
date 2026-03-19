dependencies {
    implementation(libs.jetty.server)
    implementation(project(":jwl-common"))

    testImplementation(testFixtures(project(":jwl-common")))
}
