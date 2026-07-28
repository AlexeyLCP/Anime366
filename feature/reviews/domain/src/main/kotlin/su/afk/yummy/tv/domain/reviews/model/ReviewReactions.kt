package su.afk.yummy.tv.domain.reviews.model

data class ReviewReactions(val likes: Int, val dislikes: Int, val vote: ReviewVote) {

    /**
     * Возвращает реакции после оптимистичного применения голоса [target]:
     * снимает предыдущий голос текущего пользователя и учитывает новый,
     * не давая счётчикам уйти в минус.
     */
    fun optimistic(target: ReviewVote): ReviewReactions {
        var nextLikes = likes - if (vote == ReviewVote.LIKE) 1 else 0
        var nextDislikes = dislikes - if (vote == ReviewVote.DISLIKE) 1 else 0
        if (target == ReviewVote.LIKE) nextLikes++
        if (target == ReviewVote.DISLIKE) nextDislikes++
        return copy(
            likes = nextLikes.coerceAtLeast(0),
            dislikes = nextDislikes.coerceAtLeast(0),
            vote = target,
        )
    }
}
