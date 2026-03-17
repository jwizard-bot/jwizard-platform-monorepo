dependencies {
    implementation(libs.jetty.server)
    implementation(libs.reflections)

    implementation(project(":jwl-common"))
    testImplementation(testFixtures(project(":jwl-common")))
}
