dependencies {
    implementation(project(":jwl-common"))
    implementation(project(":jwl-contracts"))
    implementation(project(":jwl-persistence"))
    implementation(project(":jwl-transport"))

    testImplementation(testFixtures(project(":jwl-common")))
}
