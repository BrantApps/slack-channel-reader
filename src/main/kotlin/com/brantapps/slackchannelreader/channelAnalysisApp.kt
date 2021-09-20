package com.brantapps.slackchannelreader

import io.reactivex.schedulers.Schedulers
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Extracts key statistics used to assess slack usage health.
 */
fun channelAnalysisApp(apiKey: String, daysBack: Long) {
    val writer = Files.newBufferedWriter(Paths.get("active-slack-channels.csv"))
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader(
                    "Channel Name",
                    "Description",
                    "Creator",
                    "Team ID",
                    "Creator Handle",
                    "Created Date",
                    "# of members",
                    "# of messages"
            )
    )
    ChannelService()
            .getChannelOverviews(apiKey, daysBack)
            .map {
                csvPrinter.printRecord(
                        it.name,
                        it.purpose,
                        it.creator.realName,
                        it.creator.team,
                        it.creator.displayName,
                        it.created,
                        it.numberOfMembers,
                        it.messagesSentInPeriod
                )
            }
            .observeOn(Schedulers.single())
            .blockingSubscribe()

    csvPrinter.flush()
    csvPrinter.close()

    val channelAttendanceWriter = Files.newBufferedWriter(Paths.get("channel-attendance.csv"))
    val channelAttendancePrinter = CSVPrinter(channelAttendanceWriter, CSVFormat.DEFAULT
            .withHeader(
                    "User",
                    "# of Channels",
                    "Channels"
            )
    )

    val map = mutableMapOf<User, MutableList<String>>()
    ChannelService()
            .getChannelAttendance(apiKey)
            .map {
                it.forEach { each ->
                    val values = map[each.key.user]
                    values?.add(each.value)
                    if (values != null) {
                        map[each.key.user] = values
                    } else {
                        map[each.key.user] = arrayListOf(each.value)
                    }
                }
            }
            .observeOn(Schedulers.single())
            .blockingSubscribe()

    map.map {
        channelAttendancePrinter.printRecord(
                it.key.profile.real_name,
                it.value.size,
                it.value
        )
    }

    channelAttendancePrinter.flush()
    channelAttendancePrinter.close()
}