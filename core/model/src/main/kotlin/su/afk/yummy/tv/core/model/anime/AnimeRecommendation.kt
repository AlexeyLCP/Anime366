package su.afk.yummy.tv.core.model.anime

data class AnimeRecommendation(
    val animeId: Int,
    val title: String,
    val poster: AnimePoster?,
    val rating: Double?,
    val type: String?,
    val year: Int?,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val vote: AnimeRecommendationVote = AnimeRecommendationVote.NONE,
) {

    /**
     * Возвращает рекомендацию после оптимистичного применения голоса [target]:
     * снимает предыдущий голос текущего пользователя и учитывает новый,
     * не давая счётчикам уйти в минус.
     */
    fun optimistic(target: AnimeRecommendationVote): AnimeRecommendation {
        var nextLikes = likes - if (vote == AnimeRecommendationVote.LIKE) 1 else 0
        var nextDislikes = dislikes - if (vote == AnimeRecommendationVote.DISLIKE) 1 else 0
        if (target == AnimeRecommendationVote.LIKE) nextLikes++
        if (target == AnimeRecommendationVote.DISLIKE) nextDislikes++
        return copy(
            likes = nextLikes.coerceAtLeast(0),
            dislikes = nextDislikes.coerceAtLeast(0),
            vote = target,
        )
    }
}
