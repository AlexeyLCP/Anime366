package su.afk.yummy.tv.android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

/**
 * Имя версии установленного APK. Живёт в app: это свойство самого приложения, а не какой-либо
 * фичи — потребители (проверка обновлений, EOL-чек) получают его через `@Named`.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppVersionModule {

    @Provides
    @Named("appVersionName")
    fun provideVersionName(@ApplicationContext context: Context): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
}
