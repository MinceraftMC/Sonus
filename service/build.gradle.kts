import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.gradleup.shadow)
}

dependencies {
    api(projects.protocol)
    api(projects.apiService)

    // included in all service implementations
    compileOnlyApi(libs.adventure.text.logger.slf4j)

    implementation(projects.network)
    api(projects.commonAdapter)

    implementation(projects.svcAdapter)
    implementation(projects.plasmoAdapter)
    implementation(projects.webAdapter)
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
