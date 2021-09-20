package com.brantapps.slackchannelreader

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class LinkToMessage(
        val channel: String,
        val permalink: String
)

class ChatService {
    interface GetPermaLink {
        @Headers("Content-Type: application/x-www-form-urlencoded")
        @GET("chat.getPermalink")
        fun getPermaLink(
                @Query("token") token: String,
                @Query("channel") channel: String,
                @Query("message_ts") message_ts: String
        ): Observable<ChatLink>
    }

    fun getPermaLink(token: String, channelId: String, ts: String): Observable<LinkToMessage> {
        return ChannelReader.retrofit.create(GetPermaLink::class.java)
                .getPermaLink(token, channelId, ts).map {
                    LinkToMessage(it.channel, it.permalink)
                }
    }
}
