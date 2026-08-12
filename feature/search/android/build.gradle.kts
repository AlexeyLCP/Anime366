plugins {
    id("yummytv.android.library")
    id("yummytv.android.hilt")
}

android {
    namespace = "su.afk.yummy.tv.feature.search.android"
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":feature:search:api"))
    implementation(project(":feature:search:domain"))

    implementation(libs.kotlinx.coroutines.android)
}
