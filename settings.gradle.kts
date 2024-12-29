rootProject.name = "IDELogin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()

        // Minecraft
        maven("https://libraries.minecraft.net") {
            name = "minecraft"
            mavenContent {
                releasesOnly()
            }
        }

        // Architectury
        maven("https://maven.architectury.dev/")

        // NeoForge
        maven("https://maven.neoforged.net/releases")

        // Fabric
        maven("https://maven.fabricmc.net/")

        // Forge
        maven("https://maven.minecraftforge.net/")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
    }

    plugins {
        id("net.kyori.blossom") version "1.2.0"
        id("net.kyori.indra")
        id("net.kyori.indra.git")
    }

    includeBuild("build-logic")
}

include(":auth")
include(":common")
include(":fabric")
include(":forge")
include(":neoforge")
