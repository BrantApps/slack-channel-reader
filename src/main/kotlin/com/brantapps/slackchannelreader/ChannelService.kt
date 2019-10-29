package com.brantapps.slackchannelreader

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.observables.GroupedObservable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.time.LocalDate
import java.util.AbstractMap
import java.util.stream.Collectors

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

class ChannelService {
    interface ListChannels {
        @Headers("Content-Type: application/x-www-form-urlencoded")
        @GET("channels.list")
        fun list(
                @Query("token") token: String,
                @Query("limit") limit: Int = 1000,
                @Query("exclude_archived") excludeArchive: Boolean = true
        ): Observable<Channels>
    }

    interface ChannelHistory {
        @Headers("Content-Type: application/x-www-form-urlencoded")
        @GET("channels.history")
        fun getHistory(
                @Query("token") token: String,
                @Query("channel") channel: String,
                @Query("oldest") oldest: Long = epochTimeInSecondsMinus() // Default look back 30 days
        ): Observable<History>
    }

    fun getChannelOverviews(token: String, daysToLookBack: Long): Observable<ChannelOverview> {
        val listObservable: Observable<Channels> = ChannelReader.retrofit.create(ChannelService.ListChannels::class.java)
                .list(token)

        fun buildHistoryObservable(token: String, channelId: String): Observable<History> {
            return ChannelReader.retrofit.create(ChannelService.ChannelHistory::class.java)
                    .getHistory(token, channelId, epochTimeInSecondsMinus(daysToLookBack))
        }

        return listObservable
                .flatMap {
                    Observable.fromIterable(it.channels)
                }
                .map { channel ->
                    val historyObservable = buildHistoryObservable(token, channel.id)
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
        val listObservable: Observable<Channels> = ChannelReader.retrofit.create(ChannelService.ListChannels::class.java)
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
}