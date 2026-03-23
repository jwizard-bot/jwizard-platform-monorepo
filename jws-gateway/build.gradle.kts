dependencies {
    implementation(project(":jwl-common"))
    implementation(project(":jwl-contracts"))
    implementation(project(":jwl-http"))
    implementation(project(":jwl-queue"))

    testImplementation(testFixtures(project(":jwl-common")))
}
