package su.afk.yummy.tv.feature.home.model

/** Разовое объявление, управляемое удалённо через feature flags. */
data class HomeAnnouncement(
    val id: String,
    val title: String?,
    val message: String,
    val buttonText: String?,
)
