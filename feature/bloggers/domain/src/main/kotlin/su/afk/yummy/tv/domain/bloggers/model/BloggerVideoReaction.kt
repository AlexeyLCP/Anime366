package su.afk.yummy.tv.domain.bloggers.model

data class BloggerVideoReaction(
    val likes: Int = 0,
    val dislikes: Int = 0,
    val vote: BloggerVideoVote = BloggerVideoVote.NONE,
) {

    /**
     * Возвращает реакции после оптимистичного применения голоса [target]:
     * снимает предыдущий голос текущего пользователя и учитывает новый,
     * не давая счётчикам уйти в минус.
     */
    fun optimistic(target: BloggerVideoVote): BloggerVideoReaction {
        var nextLikes = likes - if (vote == BloggerVideoVote.LIKE) 1 else 0
        var nextDislikes = dislikes - if (vote == BloggerVideoVote.DISLIKE) 1 else 0
        if (target == BloggerVideoVote.LIKE) nextLikes++
        if (target == BloggerVideoVote.DISLIKE) nextDislikes++
        return copy(
            likes = nextLikes.coerceAtLeast(0),
            dislikes = nextDislikes.coerceAtLeast(0),
            vote = target,
        )
    }
}
