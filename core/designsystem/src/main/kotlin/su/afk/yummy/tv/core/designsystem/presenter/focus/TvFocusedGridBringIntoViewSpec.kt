package su.afk.yummy.tv.core.designsystem.presenter.focus

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
object TvFocusedGridBringIntoViewSpec : BringIntoViewSpec {
    override fun calculateScrollDistance(
        offset: Float,
        size: Float,
        containerSize: Float,
    ): Float {
        if (containerSize <= 0f) return 0f

        // Элемент и так целиком помещается в контейнер — не подтягиваем его к пивоту:
        // иначе боковой переход фокуса внутри уже видимого ряда даёт лишний "подскролл".
        val trailingEdge = offset + size
        if (offset >= 0f && trailingEdge <= containerSize) return 0f

        val distance = if (size >= containerSize) {
            offset
        } else {
            offset - containerSize * FocusedItemPivotFraction
        }
        return if (abs(distance) <= MinScrollDistancePx) 0f else distance
    }
}

private const val FocusedItemPivotFraction = 0.12f
private const val MinScrollDistancePx = 1f
