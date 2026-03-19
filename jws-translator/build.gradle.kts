dependencies {
    implementation(project(":jwl-common"))
    implementation(project(":jwl-http"))
    implementation(project(":jwl-i18n"))

    testImplementation(testFixtures(project(":jwl-common")))
}
