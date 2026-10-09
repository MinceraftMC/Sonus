val natives: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    api(projects.commonProtocol)

    compileOnlyApi(libs.leangen)
    compileOnlyApi(libs.jspecify)
    compileOnlyApi(libs.checkerqual)
    compileOnlyApi(libs.adventure.api)
    compileOnlyApi(libs.adventure.text.serializer.gson)
    compileOnlyApi(libs.slf4j.api)
    compileOnlyApi(libs.guava)
    compileOnlyApi(libs.gson)
    compileOnlyApi(libs.bundles.configurate)

    natives(libs.lame4j) // mp3 encoder/decoder
    natives(libs.opus4j) // opus encoder/decoder
    natives(libs.speex4j) // automatic gain control
}

tasks.withType<Jar> {
    metaInf {
        from(natives.resolve()) {
            into("sonus-natives")
            // rename to common name
            rename { "${it.substringBefore('-')}.jar" }
        }
    }
}
