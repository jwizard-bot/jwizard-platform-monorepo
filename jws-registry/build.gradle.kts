dependencies {
    implementation(project(":jwl-common"))
    implementation(project(":jwl-contracts"))
    implementation(project(":jwl-http"))

    testImplementation(testFixtures(project(":jwl-common")))
}
