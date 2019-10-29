rootProject.name = "slack-channel-reader"

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-serialization") {
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
}

buildscript {
    val kotlinVersion: String by extra { "1.3.31" }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    }

    repositories {
        maven { setUrl("https://kotlin.bintray.com/kotlinx") }
        jcenter()
    }
}