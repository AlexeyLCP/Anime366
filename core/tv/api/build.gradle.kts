plugins {
    id("yummytv.android.library")
}

android {
    namespace = "su.afk.yummy.tv.core.tv.api"
}

dependencies {
    api(libs.androidx.activity)
    api(libs.kotlinx.coroutines.android)
}
