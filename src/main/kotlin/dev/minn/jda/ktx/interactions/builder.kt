/*
 * Copyright 2020 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.minn.jda.ktx.interactions

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction


inline fun Command(name: String, description: String, builder: CommandData.() -> Unit = {}) = CommandData(name, description).apply(builder)
inline fun Subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) = SubcommandData(name, description).apply(builder)
inline fun SubcommandGroup(name: String, description: String, builder: SubcommandGroupData.() -> Unit = {}) = SubcommandGroupData(name, description).apply(builder)

inline fun <reified T> Option(name: String, description: String, required: Boolean = false, builder: OptionData.() -> Unit = {}): OptionData {
    val type = optionType<T>()
    if (type == OptionType.UNKNOWN)
        throw IllegalArgumentException("Cannot resolve type " + T::class.java.simpleName + " to OptionType!")
    return OptionData(type, name, description).setRequired(required).apply(builder)
}

inline fun CommandListUpdateAction.command(name: String, description: String, builder: CommandData.() -> Unit = {}) = addCommands(Command(name, description, builder))
inline fun <reified T> CommandData.option(name: String, description: String, required: Boolean = false, builder: OptionData.() -> Unit = {}) = addOptions(Option<T>(name, description, required, builder))
inline fun <reified T> SubcommandData.option(name: String, description: String, required: Boolean = false, builder: OptionData.() -> Unit = {}) = addOptions(Option<T>(name, description, required, builder))
inline fun CommandData.subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) = addSubcommands(Subcommand(name, description, builder))
inline fun SubcommandGroupData.subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) = addSubcommands(Subcommand(name, description, builder))
inline fun CommandData.group(name: String, description: String, builder: SubcommandGroupData.() -> Unit = {}) = addSubcommandGroups(SubcommandGroup(name, description, builder))
fun OptionData.choice(name: String, value: String) = addChoice(name, value)
fun OptionData.choice(name: String, value: Int) = addChoice(name, value)

inline fun JDA.updateCommands(builder: CommandListUpdateAction.() -> Unit) = updateCommands().apply(builder)
inline fun JDA.upsertCommand(name: String, description: String, builder: CommandData.() -> Unit) = upsertCommand(Command(name, description, builder))
inline fun Guild.updateCommands(builder: CommandListUpdateAction.() -> Unit) = updateCommands().apply(builder)
inline fun Guild.upsertCommand(name: String, description: String, builder: CommandData.() -> Unit) = upsertCommand(Command(name, description, builder))

inline fun Command.editCommand(builder: CommandData.() -> Unit) = editCommand().apply(CommandData(name, description).apply(builder))

/*

/// Example usage

fun registerCommands(api: JDA) {
    api.updateCommands {
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

    api.upsertCommand("prune", "Prune messages") {
        option<Int>("amount", "The amount to delete from 2-100, default 50")
    }.queue()
}

 */