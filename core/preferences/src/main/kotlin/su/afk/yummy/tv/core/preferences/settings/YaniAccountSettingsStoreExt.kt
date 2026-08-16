package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.first

suspend fun YaniAccountSettingsStore.currentLanguageCode(): String =
    yaniContentLanguage.first().apiCode

suspend fun YaniAccountSettingsStore.currentUserId(): Int =
    yaniUserId.first()
