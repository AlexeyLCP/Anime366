package su.afk.yummy.tv.data.update.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import su.afk.yummy.tv.core.network.di.UnauthenticatedJsonClient
import su.afk.yummy.tv.data.update.UpdateConfig
import su.afk.yummy.tv.data.update.dto.GitHubReleaseDto
import su.afk.yummy.tv.data.update.mapper.toDomain
import su.afk.yummy.tv.domain.update.model.AppRelease
import su.afk.yummy.tv.domain.update.repository.UpdateRepository
import su.afk.yummy.tv.domain.update.util.isVersionNewer
import javax.inject.Inject

/**
 * Последний релиз из GitHub Releases. Клиент намеренно неавторизованный: api.github.com —
 * публичный сторонний сервис, токен приложения ему отправлять незачем.
 */
internal class GitHubUpdateRepository @Inject constructor(
    @param:UnauthenticatedJsonClient private val httpClient: HttpClient,
) : UpdateRepository {

    override suspend fun latestRelease(currentVersion: String): AppRelease? {
        val url = RELEASES_URL ?: return null
        val response: HttpResponse = httpClient.get(url) {
            header("Accept", GITHUB_ACCEPT)
        }
        if (!response.status.isSuccess()) return null

        val releases = response.body<List<GitHubReleaseDto>>().mapNotNull { it.toDomain() }
        val latest = releases.firstOrNull() ?: return null
        val updatesCount = releases.count { isVersionNewer(currentVersion, it.version) }
        return latest.copy(updatesCount = updatesCount)
    }

    private companion object {
        const val GITHUB_ACCEPT = "application/vnd.github+json"

        /** null, когда репозиторий обновлений не сконфигурирован — проверка просто выключена. */
        val RELEASES_URL: String? =
            if (UpdateConfig.GITHUB_OWNER.isNotBlank() && UpdateConfig.GITHUB_REPO.isNotBlank()) {
                "https://api.github.com/repos/" +
                        "${UpdateConfig.GITHUB_OWNER}/${UpdateConfig.GITHUB_REPO}/releases"
            } else {
                null
            }
    }
}
