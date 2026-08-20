package su.afk.yummy.tv.feature.details.utils

import kotlin.time.Duration.Companion.milliseconds

/** Пауза перед перечитыванием подписок: серверу нужно время, чтобы мутация попала в список. */
internal val SUBSCRIPTION_REFRESH_DELAY = 350.milliseconds
