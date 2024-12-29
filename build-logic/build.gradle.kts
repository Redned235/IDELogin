plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()

    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    implementation(libs.indra)
    implementation(libs.shadow)
    implementation(libs.architectury.plugin)
    implementation(libs.architectury.loom)
    implementation(libs.minotaur)
}
