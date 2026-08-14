package su.afk.yummy.tv.feature.comments.mapper

import su.afk.yummy.tv.domain.comments.model.Comment
import su.afk.yummy.tv.feature.comments.CommentsState.CommentUi

internal fun Comment.toCommentUi(): CommentUi = CommentUi(comment = this)
