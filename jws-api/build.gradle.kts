dependencies {
    implementation(project(":jwl-common"))
    implementation(project(":jwl-http"))
    implementation(project(":jwl-kv"))

    testImplementation(testFixtures(project(":jwl-common")))
}
