plugins {
    `java`
    `java-library`
    id("net.kyori.indra")
}

val minecraftVersion = project.property("minecraft_version") as String

dependencies {
    compileOnly("org.checkerframework", "checker-qual", "3.19.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 21
}

indra {
    github("Redned235", "IDELogin") {
        ci(true)
        issues(true)
        scm(true)
    }
    mitLicense()

    javaVersions {
        target(21)
    }
}

tasks {
    processResources {
        filesMatching(listOf("fabric.mod.json", "idelogin.mixins.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
            expand(
                "id" to "idelogin",
                "name" to "IDELogin",
                "version" to project.version,
                "description" to project.description,
                "author" to "Redned",
                "minecraft_version" to minecraftVersion
            )
        }
    }
}
