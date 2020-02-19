package com.dbabrovich.basic

import org.threeten.bp.LocalDateTime

/**
 * Contains information about a match
 */
data class Match(
    val id: String,
    val feedMatchId: Long,
    val homeTeamName: String,
    val homeTeamId: String,
    val homeScore: Long,
    val awayTeamName: String,
    val awayTeamId: String,
    val awayScore: Long,
    val competitionId: Long,
    val competition: String
)

/**
 * Contains basic information about a comment
 */
data class Comment(
    val type: String,
    val comment: String,
    val period: Long,
    val time: String = ""
)

data class CommentaryFeed(
    val match: Match,
    val comments: Collection<Comment>,
    val createdAt: LocalDateTime
)