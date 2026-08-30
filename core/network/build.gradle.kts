plugins {
    id("yummytv.android.library")
    alias(libs.plugins.kotlinSerialization)
    id("yummytv.android.hilt")
}

android {
    namespace = "su.afk.yummy.tv.core.network"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        val appId = providers.gradleProperty("anime365.appId").orElse("").get()
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
        buildConfigField("String", "ANIME365_APP_ID", "\"$appId\"")
    }
}

dependencies {
    implementation(project(":core:preferences"))
    implementation(project(":core:utils"))

    api(libs.ktor.client.core)
    api(libs.okhttp)

    implementation(libs.bundles.ktor.client.json)

    implementation(libs.kotlinx.serialization.json)
}
