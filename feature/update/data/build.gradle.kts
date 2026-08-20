plugins {
    id("yummytv.android.library")
    alias(libs.plugins.kotlinSerialization)
    id("yummytv.android.hilt")
}

android {
    namespace = "su.afk.yummy.tv.data.update"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":feature:update:domain"))

    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
}
