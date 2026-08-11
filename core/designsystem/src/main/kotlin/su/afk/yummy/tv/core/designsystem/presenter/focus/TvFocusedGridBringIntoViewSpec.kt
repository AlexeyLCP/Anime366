package su.afk.yummy.tv.core.designsystem.presenter.focus

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import kotlin.math.abs

/**
 * Стейтлес BringIntoViewSpec для TV-фокуса — чистая функция от (offset, size, containerSize),
 * без общего изменяемого состояния между вызовами (общий mutable object однажды уже ломал
 * скролл по всему приложению).
 *
 * [skipIfFullyVisible] — не скроллить, если элемент уже целиком помещается в контейнер; нужно,
 * чтобы переход фокуса поперёк оси скролла (например вбок внутри уже видимого ряда грида) не
 * давал лишний "подскролл".
 *
 * [centered] — если true, элемент центрируется: target = (containerSize - size) / 2, что
 * корректно учитывает размер самого элемента (в отличие от pivotFraction = 0.5f, который тянул
 * бы leading edge элемента к середине контейнера, а не центрировал бы сам элемент). Если false,
 * используется [pivotFraction] от leading edge контейнера.
 */
@OptIn(ExperimentalFoundationApi::class)
class TvPivotBringIntoViewSpec(
    private val skipIfFullyVisible: Boolean = true,
    private val centered: Boolean = false,
    private val pivotFraction: Float = FocusedItemPivotFraction,
) : BringIntoViewSpec {
    override fun calculateScrollDistance(
        offset: Float,
        size: Float,
        containerSize: Float,
    ): Float {
        if (containerSize <= 0f) return 0f

        if (skipIfFullyVisible) {
            val trailingEdge = offset + size
            if (offset >= 0f && trailingEdge <= containerSize) return 0f
        }

        val distance = when {
            size >= containerSize -> offset
            centered -> offset - (containerSize - size) / 2f
            else -> offset - containerSize * pivotFraction
        }
        return if (abs(distance) <= MinScrollDistancePx) 0f else distance
    }
}

/** Прежнее поведение: пропускает уже видимые элементы, иначе тянет к 12% от leading edge. */
val TvFocusedGridBringIntoViewSpec: BringIntoViewSpec =
    TvPivotBringIntoViewSpec(skipIfFullyVisible = true, centered = false)

/**
 * Для вертикальных гридов, где промах должен центрировать ряд (а не подтягивать его к 12%
 * сверху), но переход фокуса вбок внутри уже видимого ряда по-прежнему не должен скроллить.
 */
val TvCenteredGridBringIntoViewSpec: BringIntoViewSpec =
    TvPivotBringIntoViewSpec(skipIfFullyVisible = true, centered = true)

/**
 * Для горизонтальных каруселей, где ось скролла совпадает с осью навигации — "skip if fully
 * visible" здесь не нужен (нет поперечного перехода, который надо защищать), а без него каждая
 * сфокусированная карточка стабильно центрируется, вместо чередования "не скроллим" / "тянем к
 * 12%", которое выглядит как дёрганье.
 */
val TvCenteredCarouselBringIntoViewSpec: BringIntoViewSpec =
    TvPivotBringIntoViewSpec(skipIfFullyVisible = false, centered = true)

private const val FocusedItemPivotFraction = 0.12f
private const val MinScrollDistancePx = 1f
