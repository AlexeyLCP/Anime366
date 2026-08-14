package su.afk.yummy.tv.core.preferences.auth

import kotlinx.coroutines.flow.Flow

/**
 * Secure storage of the Yani refresh token — SharedPreferences + AndroidKeystore AES/GCM,
 * not DataStore, because the token must stay encrypted at rest.
 */
interface YaniAuthPreferences {

    val refreshToken: Flow<String>

    suspend fun setRefreshToken(token: String)

    suspend fun clearRefreshToken()
}
