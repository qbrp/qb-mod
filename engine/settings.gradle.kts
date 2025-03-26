 pluginManagement {
    repositories {
        maven("https://repo.plasmoverse.com/snapshots")
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}
