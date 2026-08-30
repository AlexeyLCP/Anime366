plugins {
    id("yummytv.android.library")
    id("yummytv.android.hilt")
}

android {
    namespace = "su.afk.yummy.tv.data.watchlater"
}

dependencies {
    implementation(project(":core:storage"))
    implementation(project(":core:utils"))
    implementation(project(":feature:watch-later:domain"))

    implementation(libs.kotlinx.coroutines.android)
}
