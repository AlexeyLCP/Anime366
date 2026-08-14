plugins {
    id("yummytv.android.library")
}

android {
    namespace = "su.afk.yummy.tv.feature.watching.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:utils"))
    implementation(project(":feature:details:domain"))
    implementation(project(":feature:home:domain"))
    implementation(project(":feature:player:domain"))

    implementation(libs.javax.inject)
}
