plugins {
    id("idelogin.platform-conventions")
}

architectury {
    common("neoforge", "fabric", "forge")
}

val modId = project.property("mod_id") as String

// loom {
//     accessWidenerPath = file("src/main/resources/$modId.accesswidener")
// }

dependencies {
    api(projects.auth)

    compileOnly(libs.mixin)

    // Only here to suppress "unknown enum constant EnvType.CLIENT" warnings.
    compileOnly(libs.fabric.loader)
}
