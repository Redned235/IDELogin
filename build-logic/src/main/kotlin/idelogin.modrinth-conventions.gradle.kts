plugins {
    id("com.modrinth.minotaur")
}

val supportedVersions = listOf(
    "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
    "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4"
)

modrinth {
    val snapshot = "SNAPSHOT" in rootProject.version.toString()

    token.set(System.getenv("MODRINTH_TOKEN") ?: "")
    projectId.set("idelogin")
    versionNumber.set("$version.b${System.getenv("BUILD_NUMBER") ?: "999"}")
    versionType.set(if (snapshot) "beta" else "release")
    changelog.set(System.getenv("CHANGELOG") ?: "")
    uploadFile.set(tasks.named("remapJar"))
    gameVersions.set(supportedVersions)
}
