package com.brantapps.slackchannelreader

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.time.LocalDate
import java.util.*

data class ChannelOverview(
        val name: String,
        val purpose: String,
        val creator: UserOverview,
        val created: LocalDate,
        val isArchived: Boolean,
        val isPrivate: Boolean,
        val numberOfMembers: Int,
        val messagesSentInPeriod: Int
)

data class MessageOverview(
        val message: String,
        val sender: UserOverview,
        val created: LocalDate,
        val permaLink: String
)

class ChannelService {
    interface ListChannels {
        @Headers("Content-Type: application/x-www-form-urlencoded")
        @GET("conversations.list")
        fun list(
                @Query("token") token: String,
                @Query("limit") limit: Int = 1000,
                @Query("exclude_archived") excludeArchive: Boolean = true
        ): Observable<Channels>
    }

    interface ChannelHistory {
        @Headers("Content-Type: application/x-www-form-urlencoded")
        @GET("conversations.history")
        fun getHistory(
                @Query("token") token: String,
                @Query("channel") channel: String,
                @Query("oldest") oldest: Long = epochTimeInSecondsMinus(), // Default look back 30 days
                @Query("limit") limit: Int? = 20,
                @Query("cursor") cursor: String?
        ): Observable<History>
    }

    fun getChannelOverviews(token: String, daysToLookBack: Long): Observable<ChannelOverview> {
        val listObservable: Observable<Channels> = ChannelReader.retrofit.create(ListChannels::class.java)
                .list(token)

        return listObservable
                .flatMap {
                    Observable.fromIterable(it.channels)
                }
                .map { channel ->
                    val historyObservable = buildHistoryObservable(token, channel.id, daysToLookBack, 20, null)
                    val userObservable = UserService().getUser(channel.creator, token)
                    Observable.zip(
                            historyObservable,
                            userObservable,
                            BiFunction<History, UserWrapper, ChannelOverview> { history: History, user: UserWrapper ->
                                ChannelOverview(
                                        channel.name,
                                        channel.purpose.value,
                                        UserOverview(
                                                user.user.profile.real_name,
                                                user.user.profile.team,
                                                user.user.profile.display_name
                                        ),
                                        epochTimeInSecondsToLocalDate(channel.created),
                                        channel.is_archived,
                                        channel.is_private,
                                        channel.num_members,
                                        history.messages!!.size
                                )
                            }
                    ).blockingSingle()
                }
    }

    fun getChannelAttendance(token: String): Observable<List<AbstractMap.SimpleEntry<UserWrapper, String>>> {
        val listObservable: Observable<Channels> = ChannelReader.retrofit.create(ListChannels::class.java)
                .list(token)

        return listObservable
                .flatMap {
                    Observable.fromIterable(it.channels)
                }
                .map { channel ->
                    channel.members!!.map { member ->
                        AbstractMap.SimpleEntry<UserWrapper, String>(
                                UserService().getUser(member, token).blockingFirst(),
                                channel.id
                        )
                    }
                }
    }

    fun getPopularChannelMessages(token: String, channelName: String, daysToLookBack: Long, minNumberOfReactions: Int): Observable<List<MessageOverview>> {
        val listObservable = ChannelReader.retrofit.create(ListChannels::class.java)
                .list(token)

        return listObservable
                .flatMap {
                    Observable.fromIterable(it.channels)
                }
                .filter { channel -> channel.name == channelName }
                .map { channel ->
                    val messagesObservable =
                            buildHistoryObservable(
                                    token,
                                    channel.id,
                                    daysToLookBack,
                                    100,
                                    null
                            ).concatMap { t: History -> Observable.just(t.messages) }
                    messagesObservable.map { messages ->
                        messages
                                .filter { message -> message.reactions != null }
                                .filter { message -> message.reactions!!.map { reaction -> reaction.users.size }.sum() >= minNumberOfReactions }
                                .map { message ->
                            val userObservable = UserService().getUser(message.user, token)
                            val linkObservable = ChatService().getPermaLink(token, channel.id, message.ts)
                            Observable.zip(
                                    Observable.just(message),
                                    userObservable,
                                    linkObservable,
                                    Function3<MessagesItem, UserWrapper, LinkToMessage, MessageOverview> {
                                        message: MessagesItem, user: UserWrapper, linkToMessage: LinkToMessage ->
                                        MessageOverview(
                                                message = message.text,
                                                sender = UserOverview(
                                                        user.user.profile.real_name,
                                                        user.user.profile.team,
                                                        user.user.profile.display_name
                                                ),
                                                created = epochTimeInSecondsToLocalDate(
                                                        message.ts.toDouble().toInt()
                                                ),
                                                permaLink = linkToMessage.permalink
                                        )
                                    }
                            ).blockingSingle()
                        }
                    }.blockingSingle()
                }
    }

    val messages = arrayListOf<MessagesItem>()
    private fun buildHistoryObservable(
            token: String,
            channelId: String,
            daysToLookBack: Long,
            limit: Int?,
            responseMetadata: ResponseMetadata?
    ): Observable<History> {
        return ChannelReader.retrofit.create(ChannelHistory::class.java)
                .getHistory(
                        token,
                        channelId,
                        epochTimeInSecondsMinus(daysToLookBack),
                        limit,
                        responseMetadata?.next_cursor
                ).concatMap { history ->
                    messages.addAll(history.messages!!.toList())
                    if (history.has_more) {
                        buildHistoryObservable(token, channelId, daysToLookBack, 100, history.response_metadata)
                    } else {
                        Observable.just(history.copy(
                                messages = messages
                        ))
                    }
                }
    }
}