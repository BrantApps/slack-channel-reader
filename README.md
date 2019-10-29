# Slack Channel Reader

Slack Channel Reader is a Kotlin command-line application to assist organisations in visualising how they are using Slack.

## Usage

```bash
./gradlew clean fatJar
java -jar slack-channel-reader-fat.jar <your-api-key> <days-to-look-back>
```
...the fat JAR appears to run differently to the run command issued by IDEs such as IntelliJ CE. Recommend using IntelliJ to run the program for best results (until I figure out why the fat JAR bundles differently!)

## Interpreting the results

Suggested plots include a bubble chart of the `active-slack-channels` CSV;
 
1. x-axis to represent the channel create date
2. y-axis be the number of messages sent in a channel over the period specified in `<days-to-look-back>`
3. bubble size be the number of members

Watch out for;

• Big bubbles high up in the chart. Having lots of channels notifying lots of people may be a source of distractions

• A trend that older channels are larger. Reassessing a channel's purpose could be useful if you see bubbles get larger as time goes back. Did the purpose of the channel evolve causing a channel to collate more people but not loose any?

![Bubble Chart](/docs/images/bubble-example.png)

In either of these scenarios can the channels be broken out into a different suffix? E.g. if a company expands from 1 to 2 sites should the old `#general` channel evolve to something like `#companyname-firstsite` and `#companyname-secondsite` whilst keeping `#companyname-general` for truly company-wide announcements?

A second plot can be made from the `channel-attendance` CSV.

Visualising how many people are in how many channels could be useful for determining whether any efforts to get the right people talking on specific topics have been successful or otherwise.

The idea here is avoiding (in the worse case) the scenario where everyone is able to chip-in on everything by virtue of being in `#eng-chat` or equivalent vague channels. Preferring smaller, more focussed groups of people solving specific problems may be more productive depending on the size of your organisation. These kinds of clusters are transient compared to the more _infrastructure-like_ channels of `#x-general` or `#x-support` and that is good. Decisions made quicker and the results locked via an archived channel for later reference.

Plot this data using a grouped histogram in buckets you feel are useful. Following on from the example that "more channels with fewer people is better" (debatable depending on your org structure) then this kind of plot will show whether your company is adopting the strategy by shifting the mean to the right and reducing the standard deviation (the spread).

![Histogram Chart](/docs/images/histogram-grouped.png)
