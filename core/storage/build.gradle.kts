plugins {
    id("yummytv.android.library")
    id("yummytv.android.hilt")
}

android {
    namespace = "su.afk.yummy.tv.core.storage"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:utils"))

    implementation(libs.bundles.room)

    add("ksp", libs.room.compiler)
}
