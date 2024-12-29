import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("idelogin.base-conventions")
    id("com.gradleup.shadow")
}

tasks {
    named<Jar>("jar") {
        archiveClassifier.set("unshaded")
        from(project.rootProject.file("LICENSE"))
    }

    val shadowJar = named<ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        archiveVersion.set("")
        archiveClassifier.set("")
    }

    named("build") {
        dependsOn(shadowJar)
    }
}
