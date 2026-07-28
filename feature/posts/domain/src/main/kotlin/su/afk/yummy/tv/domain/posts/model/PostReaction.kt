package su.afk.yummy.tv.domain.posts.model

data class PostReaction(val likes: Int, val dislikes: Int, val vote: PostVote) {

    /**
     * Возвращает реакции после оптимистичного применения голоса [target]:
     * снимает предыдущий голос текущего пользователя и учитывает новый,
     * не давая счётчикам уйти в минус.
     */
    fun optimistic(target: PostVote): PostReaction {
        var nextLikes = likes - if (vote == PostVote.LIKE) 1 else 0
        var nextDislikes = dislikes - if (vote == PostVote.DISLIKE) 1 else 0
        if (target == PostVote.LIKE) nextLikes++
        if (target == PostVote.DISLIKE) nextDislikes++
        return copy(
            likes = nextLikes.coerceAtLeast(0),
            dislikes = nextDislikes.coerceAtLeast(0),
            vote = target,
        )
    }
}
