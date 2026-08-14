plugins {
    id("yummytv.android.library")
    id("yummytv.android.hilt")
}

android {
    namespace = "su.afk.yummy.tv.core.featuretoggle"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:analytics"))
    implementation(project(":core:utils"))

    implementation(libs.varioqub.config)
    implementation(libs.varioqub.appmetrica.adapter)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.kotlinx.coroutines.android)
}
