enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Sonus"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


include("api-service")
include("protocol")
include("service")
include("agent-paper")
include("svc-protocol")
include("svc-adapter")
include("plasmo-protocol")
include("plasmo-adapter")
include("web-protocol")
include("web-adapter")
include("webrtc-pion")
include("webrtc-adapter")
include("webrtc-ipc")
include("common-service")
include("common-protocol")
include("service-velocity")
include("network")
