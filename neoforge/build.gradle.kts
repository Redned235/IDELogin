plugins {
    id("idelogin.platform-conventions")
    id("idelogin.modrinth-conventions")
}

val modId = project.property("mod_id") as String

architectury {
    platformSetupLoomIde()
    neoForge()
}

val common: Configuration by configurations.creating
// Without this, the mixin config isn't read properly with the runServer neoforge task
val developmentNeoForge: Configuration = configurations.getByName("developmentNeoForge")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentNeoForge.extendsFrom(configurations["common"])
}

dependencies {
    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    neoForge(libs.neoforge)

    transitiveInclude(libs.minecraft.auth)
    shadow(projects.auth) { isTransitive = false }
    shadow(project(path = ":common", configuration = "transformProductionNeoForge")) {
        isTransitive = false
    }

    configurations["transitiveInclude"].resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
    }
}

tasks {
    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveBaseName.set("$modId-neoforge-$version.b${System.getenv("BUILD_NUMBER") ?: "999"}")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    shadowJar {
        dependsOn(":common:shadowJar")

        configurations = listOf(project.configurations["shadow"])
    }

    jar {
        archiveClassifier.set("dev")
    }
}

sourceSets {
    main {
        resources {
            srcDirs(project(":common").sourceSets["main"].resources.srcDirs)
        }
    }
}

modrinth {
    loaders.addAll("neoforge")
}
