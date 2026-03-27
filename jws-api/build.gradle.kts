dependencies {
    runtimeOnly(libs.postgresql)
    implementation(project(":jwl-common"))
    implementation(project(":jwl-http"))
    implementation(project(":jwl-kv"))
    implementation(project(":jwl-sql"))

    testImplementation(testFixtures(project(":jwl-common")))
}
