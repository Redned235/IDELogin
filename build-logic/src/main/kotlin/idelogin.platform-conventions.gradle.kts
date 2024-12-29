plugins {
    id("idelogin.shadow-conventions")
    id("java-library")
    id("architectury-plugin")
    id("dev.architectury.loom")
}

val transitiveInclude: Configuration by configurations.creating {
    exclude(group = "com.google.code.gson")
    exclude(group = "com.google.errorprone")
    exclude(group = "org.slf4j")
}

val minecraftVersion = project.property("minecraft_version") as String

architectury {
    minecraft = minecraftVersion
}

loom {
    silentMojangMappingsLicense()
}

configurations {
    create("includeTransitive").isTransitive = true
}

repositories {
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
}

tasks {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this task, sources will not be generated.
    sourcesJar {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    shadowJar {
        // Mirrors the example fabric project, otherwise tons of dependencies are shaded that shouldn't be
        configurations = listOf(project.configurations.shadow.get())
        // The remapped shadowJar is the final desired mod jar
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("shaded")
    }
}
