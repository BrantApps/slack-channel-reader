package com.brantapps.slackchannelreader

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class UserOverview(
        val realName: String,
        val team: String,
        val displayName: String
)

class UserService {
    interface GetUserInfo {
        @Headers("Content-Type: application/x-www-form-urlencoded")
        @GET("users.info")
        fun getUser(
                @Query("user") userId: String,
                @Query("token") token: String
        ): Observable<UserWrapper>
    }

    fun getUser(id: String, token: String): Observable<UserWrapper> {
        return ChannelReader.retrofit.create(GetUserInfo::class.java).getUser(id, token)
    }
}