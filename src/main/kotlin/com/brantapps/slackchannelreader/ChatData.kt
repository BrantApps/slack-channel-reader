package com.brantapps.slackchannelreader

@kotlinx.serialization.Serializable
data class ChatLink(val ok: Boolean = false,
                    val channel: String,
                    val permalink: String)