package com.brantapps.slackchannelreader

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun epochTimeInSecondsToLocalDate(unixTimeInSeconds: Int): LocalDate {
    return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(unixTimeInSeconds.toLong()),
            TimeZone.getDefault().toZoneId()
    ).toLocalDate()
}

fun epochTimeInSecondsMinus(daysToMinus: Long = 30): Long {
    return LocalDate.now().minusDays(daysToMinus).atStartOfDay().toEpochSecond(ZoneOffset.UTC)
}