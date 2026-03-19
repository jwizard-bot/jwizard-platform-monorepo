dependencies {
    implementation(project(":jwl-common"))
    implementation(project(":jwl-contracts"))
    implementation(project(":jwl-http"))
    implementation(project(":jwl-persistence"))

    testImplementation(testFixtures(project(":jwl-common")))
}
