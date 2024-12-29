plugins {
    id("idelogin.platform-conventions")
    id("idelogin.modrinth-conventions")
}

val modId = project.property("mod_id") as String

architectury {
    platformSetupLoomIde()
    forge()
}

val common: Configuration by configurations.creating
// Without this, the mixin config isn't read properly with the runServer forge task
val developmentForge: Configuration = configurations.getByName("developmentForge")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentForge.extendsFrom(configurations["common"])
}

dependencies {
    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    forge(libs.forge)

    transitiveInclude(libs.minecraft.auth)
    shadow(projects.auth) { isTransitive = false }
    shadow(project(path = ":common", configuration = "transformProductionForge")) {
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
        archiveBaseName.set("$modId-forge-$version.b${System.getenv("BUILD_NUMBER") ?: "999"}")
        archiveClassifier.set("")
        archiveVersion.set("")
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
    loaders.addAll("forge")
}
