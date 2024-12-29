plugins {
    id("idelogin.build-logic")
    id("idelogin.base-conventions")
}

group = "me.redned.idelogin"
version = project.findProperty("version") as String? ?: "1.0.0"
