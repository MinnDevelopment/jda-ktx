
[1]: https://github.com/dv8fromtheworld/jda
[2]: https://github.com/kotlin/kotlinx.coroutines
[3]: https://github.com/MinnDevelopment/jda-reactor

[4]: https://github.com/MinnDevelopment/jda-ktx/tree/master/src/main/kotlin/dev/minn/jda/ktx/CoroutineEventManager.kt
[5]: https://github.com/MinnDevelopment/jda-reactor/tree/master/src/main/java/club/minnced/jda/reactor/ReactiveEventManager.java
[6]: https://github.com/MinnDevelopment/jda-ktx/tree/master/src/main/kotlin/dev/minn/jda/ktx/builder.kt

# jda-ktx

Collection of useful Kotlin extensions for [JDA][1].
Great in combination with [kotlinx-coroutines][2] and [jda-reactor][3].

## Examples

The most useful feature of this library is the [CoroutineEventManager][4] which adds the ability to use
suspending functions in your event handlers.

```kotlin
val jda = DefaultJDA("token") {
    memberCachePolicy = MemberCachePolicy.ONLINE or MemberCachePolicy.VOICE or MemberCachePolicy.OWNER
    chunkingFilter = ChunkingFilter.NONE
    compression = Compression.ZLIB
    largeThreshold = 250
}

// This can only be used with the CoroutineEventManager
jda.listener<MessageReceivedEvent> {
    val guild = it.guild
    val channel = it.channel
    val message = it.message
    val content = message.contentRaw

    if (content.startsWith("!profile")) {
        // Send typing indicator and wait for it to arrive
        channel.sendTyping().await()
        val user = message.mentionedUsers.firstOrNull() ?: run {
            // Try loading user through prefix loading
            val matches = guild.retrieveMembersByPrefix(content.substringAfter("!profile "), 1).await()
            // Take first result, or null
            matches.firstOrNull()
        }
        
        if (user == null) // unknown user for name
            channel.sendMessageFormat("%s, I cannot find a user for your query!", it.author).queue()
        else // load profile and send it as embed
            channel.sendMessageFormat("%s, here is the user profile:", it.author)
                .embed(profile(user)) // custom profile embed implementation
                .queue()
    }
}

jda.onCommand("ban") { event ->
    val user = event.getOption("user")!!.asUser
    val confirm = Button.danger("${user.id}:ban", "Confirm")
    event.reply("Are you sure you want to ban **${user.asTag}**?")
        .addActionRow(confirm)
        .setEphemeral(true)
        .queue()
    
    withTimeoutOrNull(60000) { // 1 minute timeout
        val pressed = event.user.awaitButton(confirm) // await for user to click button
        pressed.deferEdit().queue() // Acknowledge the button press
        event.guild.ban(user, 0).queue() // the button is pressed -> execute action
    } ?: event.hook.editOriginal("Timed out.").setActionRows(emptyList()).queue()
}

jda.onButton("hello") { // Button that says hello
    it.reply("Hello :)").queue()
}
```

### Coroutine Extensions

I've added a few suspending extension functions to various JDA components.
None of these extensions require the `CoroutineEventManager`!

To use `await<Event>` and `awaitMessage` the event manager must support either `EventListener` or `@SubscribeEvent`,
the [`ReactiveEventManager`][5] and [`CoroutineEventManager`][4] both support this.

```kotlin
/* Async Operations */

// Await RestAction result
suspend fun <T> RestAction<T>.await()
// Await Task result (retrieveMembersByPrefix)
suspend fun <T> Task<T>.await()

/* Event Waiter */

// Await specific event
suspend fun <T : GenericEvent> JDA.await(filter: (T) -> Boolean = { true })
// Await specific event
suspend fun <T : GenericEvent> ShardManager.await(filter: (T) -> Boolean = { true })
// Await message from specific channel (filter by user and/or filter function)
suspend fun MessageChannel.awaitMessage(author: User? = null, filter: (Message) -> Boolean = { true }): Message

/* Experimental Channel API */

// Coroutine iterators for PaginationAction
suspend fun <T, M: PaginationAction<T, M>> M.produce(scope: CoroutineScope = GlobalScope): ReceiverChannel<T>
// Flow representation for PaginationAction
suspend fun <T, M: PaginationAction<T, M>> M.asFlow(scope: CoroutineScope = GlobalScope): Flow<T>
```

### Delegates

This library implements [delegate properties](https://kotlinlang.org/docs/reference/delegated-properties.html) which can be used to safely keep references of JDA entities such as users/channels.
These delegates can be used with the [`ref()`](https://github.com/MinnDevelopment/jda-ktx/tree/master/src/main/kotlin/dev/minn/jda/ktx/proxies.kt) extension function:

```kotlin
class Foo(guild: Guild) {
    val guild : Guild by guild.ref()
}
```

You can also use the `SLF4J` delegate to initialize loggers.

```kotlin
object Listener : ListenerAdapter() {
    private val log by SLF4J 

    override fun onMessageReceived(event: MessageReceivedEvent) {
        log.info("[{}] {}: {}", event.channel.name, event.author.asTag, event.message.contentDispaly)
    }
}
```

### Embed- and MessageBuilders

This library also provides some useful builder alternatives which can be used instead of the default `MessageBuilder` and `EmbedBuilder` from JDA.

You can see both builders in [builders.kt][6].

**Example**

```kotlin
val embed = Embed(title="Hello Friend", description="Goodbye Friend")
```

Or the builder function style:

```kotlin
val embed = Embed {
    title = "Hello Friend"
    description = "Goodbye Friend"
    field {
        name = "How good is this example?"
        value = "5 :star:"
        inline = false
    }
    timestamp = Instant.now()
    color = 0xFF0000
}
```

### Command Builders

```kotlin
jda.updateCommands {
    command("ban", "Ban a user") {
        option<User>("user", "The user to ban", true)
        option<String>("reason", "Why to ban this user")
        option<Int>("duration", "For how long to ban this user") {
            choice("1 day", 1)
            choice("1 week", 7)
            choice("1 month", 31)
        }
    }

    command("mod", "Moderation commands") {
        subcommand("ban", "Ban a user") {
            option<User>("user", "The user to ban", true)
            option<String>("reason", "Why to ban this user")
            option<Int>("duration", "For how long to ban this user") {
                choice("1 day", 1)
                choice("1 week", 7)
                choice("1 month", 31)
            }
        }

        subcommand("prune", "Prune messages") {
            option<Int>("amount", "The amount to delete from 2-100, default 50")
        }
    }
}.queue()

jda.upsertCommand("prune", "Prune messages") {
    option<Int>("amount", "The amount to delete from 2-100, default 50")
}.queue()
```



## Download

### Gradle

```gradle
repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io/")
}

dependencies {
    implementation("net.dv8tion:JDA:${JDA_VERSION}")
    implementation("com.github.minndevelopment:jda-ktx:${COMMIT}")
}
```

### Maven

```xml
<repository>
    <id>jitpack</id>
    <name>jitpack</name>
    <url>https://jitpack.io/</url>
</repository>
<repository>
    <id>dv8tion</id>
    <name>m2-dv8tion</name>
    <url>https://m2.dv8tion.net/releases</url>
</repository>
```

```xml
<dependency>
  <groupId>net.dv8tion</groupId>
  <artifactId>JDA</artifactId>
  <version>$JDA_VERSION</version>
</dependency>
<dependency>
  <groupId>com.github.minndevelopment</groupId>
  <artifactId>jda-ktx</artifactId>
  <version>$COMMIT</version>
</dependency>
```
