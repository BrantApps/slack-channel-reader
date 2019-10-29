package com.brantapps.slackchannelreader

@kotlinx.serialization.Serializable
data class Channels(val ok: Boolean = true, val channels: List<Channel>)

@kotlinx.serialization.Serializable
data class Channel(val is_private: Boolean = false,
                   val creator: String = "",
                   val purpose: Purpose,
                   val created: Int = 0,
                   val name_normalized: String = "",
                   val unlinked: Int = 0,
                   val is_archived: Boolean = false,
                   val is_channel: Boolean = false,
                   val is_general: Boolean = false,
                   val is_shared: Boolean = false,
                   val members: List<String>?,
                   val num_members: Int = 0,
                   val name: String = "",
                   val topic: Topic,
                   val id: String = "",
                   val is_org_shared: Boolean = false)

@kotlinx.serialization.Serializable
data class Purpose(val lastSet: Int = 0,
                   val creator: String = "",
                   val value: String = "")

@kotlinx.serialization.Serializable
data class Topic(val lastSet: Int = 0,
                 val creator: String = "",
                 val value: String = "")


