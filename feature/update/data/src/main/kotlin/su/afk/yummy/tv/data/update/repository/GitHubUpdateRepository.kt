package su.afk.yummy.tv.data.update.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import su.afk.yummy.tv.core.network.di.UnauthenticatedJsonClient
import su.afk.yummy.tv.data.update.UpdateConfig
import su.afk.yummy.tv.data.update.dto.GitHubReleaseDto
import su.afk.yummy.tv.data.update.dto.UpdateManifestDto
import su.afk.yummy.tv.data.update.mapper.toDomain
import su.afk.yummy.tv.domain.update.model.AppRelease
import su.afk.yummy.tv.domain.update.repository.UpdateRepository
import su.afk.yummy.tv.domain.update.util.isVersionNewer
import javax.inject.Inject

/**
 * Последний релиз: GitHub API, затем raw/jsDelivr `update.json` — api.github.com часто
 * недоступен с ТВ в РФ.
 */
internal class GitHubUpdateRepository @Inject constructor(
    @param:UnauthenticatedJsonClient private val httpClient: HttpClient,
) : UpdateRepository {

    override suspend fun latestRelease(currentVersion: String): AppRelease? {
        val fromApi = runCatching { fromGithubApi(currentVersion) }.getOrNull()
        if (fromApi != null) return fromApi
        val manifest = runCatching { fromManifest(RAW_MANIFEST_URL) }.getOrNull()
            ?: runCatching { fromManifest(JSDELIVR_MANIFEST_URL) }.getOrNull()
            ?: return null
        return manifest.copy(
            updatesCount = if (isVersionNewer(currentVersion, manifest.version)) 1 else 0,
        )
    }

    private suspend fun fromGithubApi(currentVersion: String): AppRelease? {
        val url = RELEASES_URL ?: return null
        val response: HttpResponse = httpClient.get(url) {
            header("Accept", GITHUB_ACCEPT)
            header("User-Agent", GITHUB_USER_AGENT)
        }
        if (!response.status.isSuccess()) return null
        val releases = response.body<List<GitHubReleaseDto>>().mapNotNull { it.toDomain() }
        val latest = releases.firstOrNull() ?: return null
        val updatesCount = releases.count { isVersionNewer(currentVersion, it.version) }
        return latest.copy(updatesCount = updatesCount)
    }

    private suspend fun fromManifest(url: String): AppRelease? {
        val response: HttpResponse = httpClient.get(url) {
            header("User-Agent", GITHUB_USER_AGENT)
        }
        if (!response.status.isSuccess()) return null
        val dto = MANIFEST_JSON.decodeFromString<UpdateManifestDto>(response.bodyAsText())
        if (dto.version.isBlank() || dto.apkUrl.isBlank()) return null
        return AppRelease(
            version = dto.version.trim().trimStart('v'),
            changelog = dto.changelog,
            apkUrl = dto.apkUrl,
        )
    }

    private companion object {
        const val GITHUB_ACCEPT = "application/vnd.github+json"
        const val GITHUB_USER_AGENT = "Anime366"
        const val RAW_MANIFEST_URL =
            "https://raw.githubusercontent.com/AlexeyLCP/Anime366/main/update.json"
        const val JSDELIVR_MANIFEST_URL =
            "https://cdn.jsdelivr.net/gh/AlexeyLCP/Anime366@main/update.json"
        val MANIFEST_JSON = Json { ignoreUnknownKeys = true }

        val RELEASES_URL: String? =
            if (UpdateConfig.GITHUB_OWNER.isNotBlank() && UpdateConfig.GITHUB_REPO.isNotBlank()) {
                "https://api.github.com/repos/" +
                    "${UpdateConfig.GITHUB_OWNER}/${UpdateConfig.GITHUB_REPO}/releases"
            } else {
                null
            }
    }
}
