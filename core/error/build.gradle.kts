plugins {
    id("yummytv.android.library")
    id("yummytv.android.hilt")
}

android {
    namespace = "su.afk.yummy.tv.core.error"
}

dependencies {
    api(project(":core:error:api"))

    implementation(project(":core:analytics"))
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))

    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
}
