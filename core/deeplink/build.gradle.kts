plugins {
    id("yummytv.android.library")
    id("yummytv.android.hilt")
}

android {
    namespace = "su.afk.yummy.tv.core.deeplink"
}

dependencies {
    api(project(":core:deeplink:api"))

    implementation(project(":core:navigation"))
    implementation(libs.jetbrains.navigation3.ui)
}
