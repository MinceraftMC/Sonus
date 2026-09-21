dependencies {
    api(projects.webProtocol)
    api(projects.commonAdapter)

    api(projects.webrtcAdapter)
    runtimeOnly(projects.webrtcPion)

    compileOnly(libs.bundles.configurate)

    api(libs.netty.codec.http)

    implementation(projects.network)
}
