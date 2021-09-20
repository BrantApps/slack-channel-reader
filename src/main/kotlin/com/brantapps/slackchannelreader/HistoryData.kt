package com.brantapps.slackchannelreader

import kotlinx.serialization.Optional

@kotlinx.serialization.Serializable
data class MessagesItem(@Optional val clientMsgId: String = "",
                        val text: String,
                        val user: String,
                        val ts: String,
                        @Optional val reactions: List<Reaction>? = null)

@kotlinx.serialization.Serializable
data class Reaction(val name: String,
                    val users: List<String>,
                    val count: Int)

@kotlinx.serialization.Serializable
data class History(@Optional val messages: List<MessagesItem>? = null,
                   @Optional val has_more: Boolean = false,
                   @Optional val response_metadata: ResponseMetadata? = null,
                   val ok: Boolean = false)

@kotlinx.serialization.Serializable
data class ResponseMetadata(@Optional val next_cursor: String? = null)


