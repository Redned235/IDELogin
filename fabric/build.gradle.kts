plugins {
    id("idelogin.platform-conventions")
    id("idelogin.modrinth-conventions")
}

val modId = project.property("mod_id") as String

architectury {
    platformSetupLoomIde()
    fabric()
}

val common: Configuration by configurations.creating
val developmentFabric: Configuration = configurations.getByName("developmentFabric")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentFabric.extendsFrom(configurations["common"])
}

tasks {
    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveBaseName.set("$modId-fabric-$version.b${System.getenv("BUILD_NUMBER") ?: "999"}")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    jar {
        archiveClassifier.set("dev")
    }
}

dependencies {
    modImplementation(libs.fabric.loader)
    modApi(libs.fabric.api)

    common(project(":common", configuration = "namedElements")) { isTransitive = false }

    transitiveInclude(libs.minecraft.auth)
    shadow(projects.auth) { isTransitive = false }
    shadow(project(path = ":common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }

    configurations["transitiveInclude"].resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
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
    loaders.addAll("fabric")
}
