pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Daily Bots Android Demo"
include(":daily-bots-android-demo")

/*
include(":rtvi-client-android-daily")
project(":rtvi-client-android-daily").projectDir = file("/../../rtvi-client-android-daily/rtvi-client-android-daily")
 */
