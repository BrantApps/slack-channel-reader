package com.brantapps.slackchannelreader

@kotlinx.serialization.Serializable
data class User(val tz: String = "",
                val profile: Profile)


@kotlinx.serialization.Serializable
data class UserWrapper(val ok: Boolean = false,
                       val user: User)


@kotlinx.serialization.Serializable
data class Profile(val real_name: String = "",
                   val team: String = "",
                   val display_name: String = "")


