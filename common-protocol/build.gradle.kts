dependencies {
    api(projects.common)

    compileOnlyApi(libs.jspecify)
    compileOnlyApi(libs.checkerqual)

    compileOnlyApi(libs.slf4j.api)
    compileOnlyApi(libs.netty.handler)
    compileOnlyApi(libs.leangen)

    compileOnlyApi(libs.gson)
    compileOnlyApi(libs.bundles.configurate)
    compileOnlyApi(libs.guava)

    compileOnlyApi(libs.adventure.api)
    compileOnlyApi(libs.adventure.text.serializer.gson)
}
