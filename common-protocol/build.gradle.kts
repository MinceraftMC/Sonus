dependencies {
    compileOnlyApi(libs.jspecify)
    compileOnlyApi(libs.checkerqual)

    compileOnlyApi(libs.slf4j.api)
    compileOnlyApi(libs.netty.handler)
    compileOnlyApi(libs.leangen)

    compileOnlyApi(projects.apiService)
    compileOnlyApi(libs.adventure.api)
}
