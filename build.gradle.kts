plugins {
    id("kotlinx-serialization") version "1.3.11"
    id("org.jetbrains.kotlin.jvm").version("1.3.21")
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.apache.commons:commons-csv:1.6")
    implementation("com.squareup.okhttp3:logging-interceptor:3.10.0")
    implementation("io.reactivex.rxjava2:rxjava:2.1.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.4.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "com.brantapps.slackchannelreader.AppKt"
}
