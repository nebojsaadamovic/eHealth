pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        mavenLocal()
        maven { setUrl( "https://git.nites.rs/api/v4/projects/30/packages/maven") }
    }
}

rootProject.name = "E_Zdravlje"
include(":app")
 