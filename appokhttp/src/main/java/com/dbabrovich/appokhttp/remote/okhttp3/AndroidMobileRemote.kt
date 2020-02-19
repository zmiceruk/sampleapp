package com.dbabrovich.appokhttp.remote.okhttp3

import com.dbabrovich.domain.Comment
import com.dbabrovich.domain.CommentaryFeed
import com.dbabrovich.domain.Match
import com.dbabrovich.domain.remote.MobileRemote
import io.reactivex.Single
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*

class AndroidMobileRemote(private val mobileApi: MobileApi) : MobileRemote {

    override fun getCommentary(): Single<CommentaryFeed> =
        mobileApi.getFeed()
            .map { json ->
                //Map from json into app models.
                CommentaryFeed(
                    createdAt = json.metadata().createdAt().toLocalDateTime(),
                    match = with(json.match()) {
                        Match(
                            id = id(),
                            feedMatchId = feedMatchId(),
                            homeTeamName = homeTeamName(),
                            homeTeamId = homeTeamId(),
                            homeScore = homeScore(),
                            awayTeamName = awayTeamName(),
                            awayTeamId = awayTeamId(),
                            awayScore = awayScore(),
                            competitionId = competitionId(),
                            competition = competition()
                        )
                    },
                    comments = json.match().comments().map { jsonComment ->
                        Comment(
                            type = jsonComment.type(),
                            comment = jsonComment.comment(),
                            period = jsonComment.period(),
                            time = jsonComment.time() ?: ""
                        )
                    }

                )
            }

}

/**
 * Function to convert date to [LocalDateTime]
 */
private fun Date.toLocalDateTime(): LocalDateTime {
    return DateTimeUtils.toInstant(this).atZone(
        ZoneId.systemDefault()
    ).toLocalDateTime()
}