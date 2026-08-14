package su.afk.yummy.tv.core.update.api

import su.afk.yummy.tv.core.update.github.GitHubReleaseDto

interface UpdateChecker {
    suspend fun getLatestRelease(): GitHubReleaseDto?
}
