package su.afk.yummy.tv.core.utils.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoApplicationScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultApplicationScope
