dependencies {
    implementation(libs.jedis)
    implementation(project(":jwl-common"))

    testImplementation(testFixtures(project(":jwl-common")))
}
