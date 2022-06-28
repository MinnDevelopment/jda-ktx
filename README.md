
[1]: https://github.com/dv8fromtheworld/jda
[2]: https://github.com/kotlin/kotlinx.coroutines
[3]: https://github.com/MinnDevelopment/jda-reactor

[4]: https://github.com/MinnDevelopment/jda-ktx/blob/master/src/main/kotlin/dev/minn/jda/ktx/events/CoroutineEventManager.kt
[5]: https://github.com/MinnDevelopment/jda-reactor/tree/master/src/main/java/club/minnced/jda/reactor/ReactiveEventManager.java
[6]: https://github.com/MinnDevelopment/jda-ktx/blob/master/src/main/kotlin/dev/minn/jda/ktx/messages/builder.kt
[7]: https://github.com/MinnDevelopment/strumbot
[8]: https://minndevelopment.github.io/jda-ktx/

[![Kotlin](https://img.shields.io/badge/kotlin-1.6.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![kotlinx-coroutines](https://img.shields.io/badge/kotlinx.coroutines-1.6.1-blue.svg?logo=kotlin)][2]
[![JDA](https://img.shields.io/badge/JDA-5.0.0--alpha.13-blue.svg)][1]
[![docs](https://img.shields.io/github/deployments/minndevelopment/jda-ktx/github-pages?label=docs)][8]

# jda-ktx

Collection of useful Kotlin extensions for [JDA][1].
Great in combination with [kotlinx-coroutines][2] and [jda-reactor][3].

## Required Dependencies

- Kotlin **1.6.21**
- kotlinx.coroutines **1.6.1**
- JDA **5.0.0-alpha.13**

## Examples

You can look at my own bot ([strumbot][7]) for inspiration, or look at the examples listed here.
The most useful feature of this library is the [CoroutineEventManager][4] which adds the ability to use
suspending functions in your event handlers.

```kotlin
// enableCoroutines (default true) changes the event manager to CoroutineEventManager
// this event manager uses a default scope generated by getDefaultScope() but can be configured to use a custom scope if you set it manually
val jda = light("token", enableCoroutines=true) {
    intents += listOf(GatewayIntent.GUILD_MEMBERS)
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
            channel.send("${it.author.asMention}, I cannot find a user for your query!").queue()
        else // load profile and send it as embed
            channel.send("${it.author.asMention}, here is the user profile:", embed=profile(user)).queue()
    }
}

jda.onCommand("ban", timeout=2.minutes) { event -> // 2 minute timeout listener
    val user = event.getOption<User>("user")!!
    val confirm = danger("${user.id}:ban", "Confirm")
    event.reply_(
        "Are you sure you want to ban **${user.asTag}**?",
        components=confirm.into(),
        ephemeral=true
    ).queue()

    withTimeoutOrNull(1.minutes) { // 1 minute scoped timeout
        val pressed = event.user.awaitButton(confirm) // await for user to click button
        pressed.deferEdit().queue() // Acknowledge the button press
        event.guild.ban(user, 0).queue() // the button is pressed -> execute action
    } ?: event.hook.editMessage(/*id="@original" is default */content="Timed out.", components=emptyList()).queue()
}

jda.onButton("hello") { // Button that says hello
    it.reply_("Hello :)").queue()
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

// Flow representation for PaginationAction
fun <T, M: PaginationAction<T, M>> M.asFlow(): Flow<T>
```

### Delegates

This library implements [delegate properties](https://kotlinlang.org/docs/reference/delegated-properties.html) which can be used to safely keep references of JDA entities such as users/channels.
These delegates can be used with the [`ref()`](https://github.com/MinnDevelopment/jda-ktx/blob/master/src/main/kotlin/dev/minn/jda/ktx/util/proxies.kt) extension function:

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
        log.info("[{}] {}: {}", event.channel.name, event.author.asTag, event.message.contentDisplay)
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

val message = Message {
    embeds += Embed("Ban Confirmation")
    components += row(
        success(id="approve:ban:$userId", label="Approve"),
        danger(id="deny:ban:$userId", label="Deny")
    )
}
```

### Command and SelectMenu Builders

```kotlin
jda.updateCommands {
    slash("ban", "Ban a user") {
        restrict(guild=true, Permission.BAN_MEMBERS) // guild only and requires ban permission
        option<User>("user", "The user to ban", true)
        option<String>("reason", "Why to ban this user")
        option<Int>("duration", "For how long to ban this user") {
            choice("1 day", 1)
            choice("1 week", 7)
            choice("1 month", 31)
        }
    }

    slash("mod", "Moderation commands") {
        restrict(guild=true, Permission.MODERATE_MEMBERS) // you cannot apply this on subcommands due to discord's design!
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
    restrict(guild=true, Permission.MESSAGE_MANAGE) // guild only and requires message manage perms
    option<Int>("amount", "The amount to delete from 2-100, default 50")
}.queue()

val menu = SelectMenu("menu:class") {
    option("Frost Mage", "mage-frost", emoji=FROST_SPEC, default=true)
    option("Fire Mage", "mage-fire", emoji=FIRE_SPEC)
    option("Arcane Mage", "mage-arcane", emoji=ARCANE_SPEC)
}
```


### Buttons

```kotlin
jda.upsertCommand("ban", "ban a user") {
    option<Member>("member", "The member to ban", true)
    option<String>("reason", "The ban reason")
}.queue()

jda.onCommand("ban") { event ->
    if (event.user.asTag != "Minn#6688") return@onCommand
    val guild = event.guild!!
    val member = event.getOption<User>("member")!!
    val reason = event.getOption<String>("reason")

    // Buttons will timeout after 15 minutes by default
    val accept = jda.button(label = "Accept", style = SUCCESS, user = event.user) {
        guild.ban(member, 0, reason).queue()
        it.editMessage("${event.user.asTag} banned ${member.asTag}")
            .setActionRows() // remove buttons from message
            .queue()
    }
    val deny = jda.button(label = "Deny", style = DANGER, user = event.user) { butt ->
        butt.hook.deleteOriginal().queue() // automatically acknowledged if callback does not do it
    }

    event.reply_("Are you sure?")
        .addActionRow(accept, deny) // send your buttons
        .queue()
}

// or a global listener
jda.onButton("accept") { event ->
    event.reply("You accepted :)").queue()
}
```

### Sending Messages

This library also adds some more kotlin idiomatic message send/edit extensions which rely on named parameters.
These named parameters also support defaults, which can be modified by `SendDefaults` and `MessageEditDefaults`.

In order to avoid overload conflicts with methods from JDA, some functions may use a suffixed `_` such as `reply_` or `editMessage_`.
This is simply done to prevent you from accidentally calling the wrong overload which doesn't use the defaults of this library.
If you don't care about that, you can simply add an import alias with `import dev.minn.jda.ktx.message.reply_ as reply`.

Example:

```kt
SendDefaults.ephemeral = true // <- all reply_ calls set ephemeral=true by default
MessageEditDefaults.replace = false // <- only apply explicitly set parameters (default behavior)
jda.onCommand("ban") { event ->
    if (!event.member.hasPermission(Permission.BAN_MEMBERS))
        return@onCommand event.reply_("You can't do that!").queue()
    
    event.reply_("Are you sure?", components=danger("ban", "Yes!").into())
    val interaction = event.user.awaitButton("ban")
    val user = event.getOption<User>("target")!!
    event.guild!!.ban(user, 0).queue()
    interaction.editMessage_("Successfully banned user", components=emptyList()).queue()
}
```


## Download

### Gradle

```gradle
repositories {
    mavenCentral()
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
