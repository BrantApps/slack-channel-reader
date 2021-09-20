package com.brantapps.slackchannelreader

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Dumps an entire channel to CSV.
 */
fun dumpPopularMessagesApp(apiKey: String, channelName: String, daysToLookBack: Long, minNumberOfReactions: Int) {
    val writer = Files.newBufferedWriter(Paths.get("${channelName}-messages.csv"))
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader(
                    "Date Time",
                    "Sender",
                    "Message",
                    "Link"
            )
    )
    ChannelService()
            .getPopularChannelMessages(apiKey, channelName, daysToLookBack, minNumberOfReactions)
            .flatMap { messages ->
                println("found ${messages.size} messages w/ >= ${minNumberOfReactions} reactions")
                Observable.fromIterable(messages)
             }
            .map { parentMessage ->
                csvPrinter.printRecord(
                        parentMessage.created,
                        parentMessage.sender.realName,
                        parentMessage.message,
                        parentMessage.permaLink
                )
            }
            .observeOn(Schedulers.single())
            .blockingSubscribe()

    csvPrinter.flush()
    csvPrinter.close()
}