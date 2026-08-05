package su.afk.yummy.tv.feature.faq.mobile.model

class FaqState {
    data object State

    sealed interface Event {
        data object BackSelected : Event
    }

    sealed interface Effect
}
