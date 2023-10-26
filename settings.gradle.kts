pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "SWallet"
include(":app")


include(":constant")
project(":constant").projectDir = file("../1CorePublic/constant")

include(":library:job")
project(":library:job").projectDir = file("../1CorePublic/lib-job")
include(":library:task")
project(":library:task").projectDir = file("../1CorePublic/lib-task")
include(":library:state")
project(":library:state").projectDir = file("../1CorePublic/lib-state")
include(":library:adapter")
project(":library:adapter").projectDir = file("../1CorePublic/lib-adapter")
include(":library:config")
project(":library:config").projectDir = file("../1CorePublic/lib-config")

include(":library:core")
project(":library:core").projectDir = file("../1CorePublic/lib-core")
include(":library:core:app")
project(":library:core:app").projectDir = file("../1CorePublic/lib-core-app")
include(":library:core:navigation")
project(":library:core:navigation").projectDir = file("../1CorePublic/lib-core-navigation")

include(":library:bottomsheet")
project(":library:bottomsheet").projectDir = file("../Android-HackBottomSheet/lib-bottomsheet")

include(":library:analytics")
project(":library:analytics").projectDir = file("../1CorePublic/lib-analytics")
//include(":library:analytics-sentry")
//project(":library:analytics-sentry").projectDir = file("../1CorePublic/lib-analytics-sentry")

include(":library:crashlytics")
project(":library:crashlytics").projectDir = file("../1CorePublic/lib-crashlytics")
//include(":library:crashlytics-sentry")
//project(":library:crashlytics-sentry").projectDir = file("../1CorePublic/lib-crashlytics-sentry")

include(":library:web3")
project(":library:web3").projectDir = file("../Web3Ktx/web3")
