package su.afk.yummy.tv.android.di

import androidx.navigation3.runtime.NavKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.yummy.tv.android.di.UniversalNavigationModule.Companion.provideRootTabs
import su.afk.yummy.tv.core.deeplink.api.DeepLinkResolver
import su.afk.yummy.tv.core.navigation.registrar.MobileUi
import su.afk.yummy.tv.core.navigation.registrar.NavRegistrar
import su.afk.yummy.tv.core.navigation.registrar.TvUi
import su.afk.yummy.tv.core.navigation.root.RootTab
import su.afk.yummy.tv.feature.account.IAccountNavigator
import su.afk.yummy.tv.feature.account.navigator.AccountDestination
import su.afk.yummy.tv.feature.account.navigator.AccountNavigator
import su.afk.yummy.tv.feature.bloggers.IBloggerVideosNavigator
import su.afk.yummy.tv.feature.bloggers.navigator.BloggerVideosNavigator
import su.afk.yummy.tv.feature.collection.ICollectionNavigator
import su.afk.yummy.tv.feature.collection.navigator.CollectionNavigator
import su.afk.yummy.tv.feature.collection.navigator.CollectionsCatalogDestination
import su.afk.yummy.tv.feature.comments.ICommentsNavigator
import su.afk.yummy.tv.feature.comments.navigator.CommentsNavigator
import su.afk.yummy.tv.feature.details.IDetailsNavigator
import su.afk.yummy.tv.feature.details.deeplink.DetailsDeepLinkResolver
import su.afk.yummy.tv.feature.details.navigator.DetailsNavigator
import su.afk.yummy.tv.feature.faq.IFaqNavigator
import su.afk.yummy.tv.feature.faq.navigator.FaqNavigator
import su.afk.yummy.tv.feature.home.IHomeNavigator
import su.afk.yummy.tv.feature.home.navigator.HomeDestination
import su.afk.yummy.tv.feature.home.navigator.HomeNavigator
import su.afk.yummy.tv.feature.library.ILibraryNavigator
import su.afk.yummy.tv.feature.library.navigator.LibraryDestination
import su.afk.yummy.tv.feature.library.navigator.LibraryNavigator
import su.afk.yummy.tv.feature.messages.IMessagesNavigator
import su.afk.yummy.tv.feature.messages.navigator.MessagesNavigator
import su.afk.yummy.tv.feature.pages.ISitePagesNavigator
import su.afk.yummy.tv.feature.pages.navigator.SitePagesNavigator
import su.afk.yummy.tv.feature.player.IPlayerNavigator
import su.afk.yummy.tv.feature.player.navigator.PlayerNavigator
import su.afk.yummy.tv.feature.posts.IPostsNavigator
import su.afk.yummy.tv.feature.posts.navigator.PostsDestination
import su.afk.yummy.tv.feature.posts.navigator.PostsNavigator
import su.afk.yummy.tv.feature.reviews.IReviewsNavigator
import su.afk.yummy.tv.feature.reviews.navigator.ReviewsNavigator
import su.afk.yummy.tv.feature.schedule.IScheduleNavigator
import su.afk.yummy.tv.feature.schedule.navigator.ScheduleDestination
import su.afk.yummy.tv.feature.schedule.navigator.ScheduleNavigator
import su.afk.yummy.tv.feature.search.ISearchNavigator
import su.afk.yummy.tv.feature.search.navigator.SearchDestination
import su.afk.yummy.tv.feature.search.navigator.SearchNavigator
import su.afk.yummy.tv.feature.settings.ISettingsNavigator
import su.afk.yummy.tv.feature.settings.navigator.SettingsDestination
import su.afk.yummy.tv.feature.settings.navigator.SettingsNavigator
import su.afk.yummy.tv.feature.top.ITopNavigator
import su.afk.yummy.tv.feature.top.navigator.TopDestination
import su.afk.yummy.tv.feature.top.navigator.TopNavigator
import su.afk.yummy.tv.feature.update.ui.navigator.UpdateNavRegistrar
import su.afk.yummy.tv.feature.videodownload.IVideoDownloadNavigator
import su.afk.yummy.tv.feature.videodownload.deeplink.VideoDownloadDeepLinkResolver
import su.afk.yummy.tv.feature.videodownload.navigator.VideoDownloadNavigator
import su.afk.yummy.tv.feature.watchlater.IWatchLaterNavigator
import su.afk.yummy.tv.feature.watchlater.navigator.WatchLaterNavigator
import javax.inject.Singleton

/**
 * Собирает навигаторы и регистраторы экранов всех фич в общий Hilt-граф. Реализации — простые
 * классы без зависимостей (`@Inject constructor()`), поэтому связывание через [Binds] вместо
 * ручного `@Provides fun ... = XxxImpl()`: Dagger сам вызывает конструктор, а не мы руками.
 *
 * У mobile- и tv-регистраторов одной и той же фичи одинаковое простое имя класса (`XxxNavRegistrar`
 * в разных пакетах) — `import ... as Alias` для них не работает (KSP не резолвит такой алиас в
 * сигнатуре `@Binds`), поэтому в сигнатурах ниже эти типы указаны полным именем.
 *
 * [provideRootTabs] — исключение: она строит [Map], а не связывает интерфейс с реализацией,
 * такое `@Binds` выразить не может, поэтому это `@Provides` в companion object (Kotlin-модуль
 * с `@Binds` не может держать `@Provides` как обычный метод интерфейса).
 */
@Module
@InstallIn(SingletonComponent::class)
interface UniversalNavigationModule {

    @Binds
    @Singleton
    fun bindAccountNavigator(impl: AccountNavigator): IAccountNavigator

    @Binds
    @Singleton
    fun bindMessagesNavigator(impl: MessagesNavigator): IMessagesNavigator

    @Binds
    @Singleton
    fun bindCollectionNavigator(impl: CollectionNavigator): ICollectionNavigator

    @Binds
    @Singleton
    fun bindCommentsNavigator(impl: CommentsNavigator): ICommentsNavigator

    @Binds
    @Singleton
    fun bindDetailsNavigator(impl: DetailsNavigator): IDetailsNavigator

    @Binds
    @Singleton
    fun bindFaqNavigator(impl: FaqNavigator): IFaqNavigator

    @Binds
    @Singleton
    fun bindSitePagesNavigator(impl: SitePagesNavigator): ISitePagesNavigator

    @Binds
    @Singleton
    fun bindHomeNavigator(impl: HomeNavigator): IHomeNavigator

    @Binds
    @Singleton
    fun bindLibraryNavigator(impl: LibraryNavigator): ILibraryNavigator

    @Binds
    @Singleton
    fun bindPlayerNavigator(impl: PlayerNavigator): IPlayerNavigator

    @Binds
    @Singleton
    fun bindPostsNavigator(impl: PostsNavigator): IPostsNavigator

    @Binds
    @Singleton
    fun bindReviewsNavigator(impl: ReviewsNavigator): IReviewsNavigator

    @Binds
    @Singleton
    fun bindBloggerVideosNavigator(impl: BloggerVideosNavigator): IBloggerVideosNavigator

    @Binds
    @Singleton
    fun bindScheduleNavigator(impl: ScheduleNavigator): IScheduleNavigator

    @Binds
    @Singleton
    fun bindSearchNavigator(impl: SearchNavigator): ISearchNavigator

    @Binds
    @Singleton
    fun bindSettingsNavigator(impl: SettingsNavigator): ISettingsNavigator

    @Binds
    @Singleton
    fun bindTopNavigator(impl: TopNavigator): ITopNavigator

    @Binds
    @Singleton
    fun bindVideoDownloadNavigator(impl: VideoDownloadNavigator): IVideoDownloadNavigator

    @Binds
    @Singleton
    fun bindWatchLaterNavigator(impl: WatchLaterNavigator): IWatchLaterNavigator

    // Диалог обновления один на обе платформы, поэтому регистратор попадает в оба набора.
    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileUpdateNavRegistrar(impl: UpdateNavRegistrar): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvUpdateNavRegistrar(impl: UpdateNavRegistrar): NavRegistrar

    @Binds
    @IntoSet
    fun bindDetailsDeepLinkResolver(impl: DetailsDeepLinkResolver): DeepLinkResolver

    @Binds
    @IntoSet
    fun bindVideoDownloadDeepLinkResolver(impl: VideoDownloadDeepLinkResolver): DeepLinkResolver

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileAccountNavRegistrar(
        impl: su.afk.yummy.tv.feature.account.mobile.navigator.AccountNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileCollectionNavRegistrar(
        impl: su.afk.yummy.tv.feature.collection.mobile.navigator.CollectionNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileCommentsNavRegistrar(
        impl: su.afk.yummy.tv.feature.comments.mobile.navigator.CommentsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileDetailsNavRegistrar(
        impl: su.afk.yummy.tv.feature.details.mobile.navigator.DetailsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileFaqNavRegistrar(
        impl: su.afk.yummy.tv.feature.faq.mobile.navigator.FaqNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileSitePagesNavRegistrar(
        impl: su.afk.yummy.tv.feature.pages.mobile.navigator.SitePagesNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileHomeNavRegistrar(
        impl: su.afk.yummy.tv.feature.home.mobile.navigator.HomeNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileLibraryNavRegistrar(
        impl: su.afk.yummy.tv.feature.library.mobile.navigator.LibraryNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileMessagesNavRegistrar(
        impl: su.afk.yummy.tv.feature.messages.mobile.navigator.MessagesNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileReviewsNavRegistrar(
        impl: su.afk.yummy.tv.feature.reviews.mobile.navigator.ReviewsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileBloggerVideosNavRegistrar(
        impl: su.afk.yummy.tv.feature.bloggers.mobile.navigator.BloggerVideosNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobilePlayerNavRegistrar(
        impl: su.afk.yummy.tv.feature.player.mobile.navigator.PlayerNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobilePostsNavRegistrar(
        impl: su.afk.yummy.tv.feature.posts.mobile.navigator.PostsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileScheduleNavRegistrar(
        impl: su.afk.yummy.tv.feature.schedule.mobile.navigator.ScheduleNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileSearchNavRegistrar(
        impl: su.afk.yummy.tv.feature.search.mobile.navigator.SearchNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileSettingsNavRegistrar(
        impl: su.afk.yummy.tv.feature.settings.mobile.navigator.SettingsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileTopNavRegistrar(
        impl: su.afk.yummy.tv.feature.top.mobile.navigator.TopNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileVideoDownloadNavRegistrar(
        impl: su.afk.yummy.tv.feature.videodownload.mobile.navigator.VideoDownloadNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @MobileUi
    fun bindMobileWatchLaterNavRegistrar(
        impl: su.afk.yummy.tv.feature.watchlater.mobile.navigator.WatchLaterNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvAccountNavRegistrar(
        impl: su.afk.yummy.tv.feature.account.tv.navigator.AccountNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvCollectionNavRegistrar(
        impl: su.afk.yummy.tv.feature.collection.tv.navigator.CollectionNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvCommentsNavRegistrar(
        impl: su.afk.yummy.tv.feature.comments.tv.navigator.CommentsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvDetailsNavRegistrar(
        impl: su.afk.yummy.tv.feature.details.tv.navigator.DetailsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvHomeNavRegistrar(
        impl: su.afk.yummy.tv.feature.home.tv.navigator.HomeNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvLibraryNavRegistrar(
        impl: su.afk.yummy.tv.feature.library.tv.navigator.LibraryNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvReviewsNavRegistrar(
        impl: su.afk.yummy.tv.feature.reviews.tv.navigator.ReviewsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvBloggerVideosNavRegistrar(
        impl: su.afk.yummy.tv.feature.bloggers.tv.navigator.BloggerVideosNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvPlayerNavRegistrar(
        impl: su.afk.yummy.tv.feature.player.tv.navigator.PlayerNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvPostsNavRegistrar(
        impl: su.afk.yummy.tv.feature.posts.tv.navigator.PostsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvScheduleNavRegistrar(
        impl: su.afk.yummy.tv.feature.schedule.tv.navigator.ScheduleNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvSearchNavRegistrar(
        impl: su.afk.yummy.tv.feature.search.tv.navigator.SearchNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvSettingsNavRegistrar(
        impl: su.afk.yummy.tv.feature.settings.tv.navigator.SettingsNavRegistrar,
    ): NavRegistrar

    @Binds
    @IntoSet
    @TvUi
    fun bindTvTopNavRegistrar(
        impl: su.afk.yummy.tv.feature.top.tv.navigator.TopNavRegistrar,
    ): NavRegistrar

    companion object {
        @Provides
        fun provideRootTabs(): @JvmSuppressWildcards Map<RootTab, NavKey> = mapOf(
            RootTab.ACCOUNT to AccountDestination,
            RootTab.SEARCH to SearchDestination(),
            RootTab.HOME to HomeDestination,
            RootTab.POSTS to PostsDestination,
            RootTab.COLLECTIONS to CollectionsCatalogDestination,
            RootTab.SCHEDULE to ScheduleDestination,
            RootTab.TOP to TopDestination,
            RootTab.LIBRARY to LibraryDestination,
            RootTab.SETTINGS to SettingsDestination,
        )
    }
}
