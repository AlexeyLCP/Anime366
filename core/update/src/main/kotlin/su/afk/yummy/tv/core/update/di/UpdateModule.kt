package su.afk.yummy.tv.core.update.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.update.api.ApkDownloader
import su.afk.yummy.tv.core.update.api.ApkInstaller
import su.afk.yummy.tv.core.update.api.UpdateChecker
import su.afk.yummy.tv.core.update.apk.ApkDownloaderImpl
import su.afk.yummy.tv.core.update.apk.ApkInstallerImpl
import su.afk.yummy.tv.core.update.github.GitHubUpdateChecker
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UpdateModule {

    @Binds
    @Singleton
    fun bindUpdateChecker(implementation: GitHubUpdateChecker): UpdateChecker

    @Binds
    @Singleton
    fun bindApkDownloader(implementation: ApkDownloaderImpl): ApkDownloader

    @Binds
    @Singleton
    fun bindApkInstaller(implementation: ApkInstallerImpl): ApkInstaller

    companion object {
        @Provides
        @Named("appVersionName")
        fun provideVersionName(@ApplicationContext context: Context): String =
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
    }
}
