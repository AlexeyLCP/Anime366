plugins {
    id("yummytv.android.library")
}

android {
    namespace = "su.afk.yummy.tv.core.error.api"
}

dependencies {
    api(project(":core:model"))
    api(libs.jetbrains.navigation3.ui)
}
