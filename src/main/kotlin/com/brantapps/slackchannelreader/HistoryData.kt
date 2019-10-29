package com.brantapps.slackchannelreader

@kotlinx.serialization.Serializable
data class MessagesItem(val clientMsgId: String = "")

@kotlinx.serialization.Serializable
data class History(val messages: List<MessagesItem>?,
                   val hasMore: Boolean = false,
                   val ok: Boolean = false)


