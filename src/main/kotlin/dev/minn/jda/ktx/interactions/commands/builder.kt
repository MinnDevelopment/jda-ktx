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

package dev.minn.jda.ktx.interactions.commands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.*
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction


inline fun Command(name: String, description: String, builder: SlashCommandData.() -> Unit = {}) = Commands.slash(
    name,
    description
).apply(builder)
inline fun Subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) = SubcommandData(name, description).apply(builder)
inline fun SubcommandGroup(name: String, description: String, builder: SubcommandGroupData.() -> Unit = {}) = SubcommandGroupData(name, description).apply(builder)

inline fun <reified T> Option(name: String, description: String, required: Boolean = false, autocomplete: Boolean = false, builder: OptionData.() -> Unit = {}): OptionData {
    val type = optionType<T>()
    if (type == OptionType.UNKNOWN)
        throw IllegalArgumentException("Cannot resolve type " + T::class.java.simpleName + " to OptionType!")
    return OptionData(type, name, description).setRequired(required).setAutoComplete(autocomplete).apply(builder)
}

fun CommandListUpdateAction.user(name: String) = addCommands(Commands.user(name))
fun CommandListUpdateAction.message(name: String) = addCommands(Commands.message(name))

inline fun CommandListUpdateAction.slash(name: String, description: String, builder: SlashCommandData.() -> Unit = {}) = addCommands(Command(name, description, builder))
inline fun SlashCommandData.subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) = addSubcommands(Subcommand(name, description, builder))
inline fun SubcommandGroupData.subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) = addSubcommands(Subcommand(name, description, builder))
inline fun SlashCommandData.group(name: String, description: String, builder: SubcommandGroupData.() -> Unit = {}) = addSubcommandGroups(SubcommandGroup(name, description, builder))

inline fun <reified T> SlashCommandData.option(
    name: String, description: String,
    required: Boolean = false, autocomplete: Boolean = false,
    builder: OptionData.() -> Unit = {}
) = addOptions(Option<T>(name, description, required, autocomplete, builder))
inline fun <reified T> SubcommandData.option(
    name: String, description: String,
    required: Boolean = false, autocomplete: Boolean = false,
    builder: OptionData.() -> Unit = {}
) = addOptions(Option<T>(name, description, required, autocomplete, builder))

fun CommandListUpdateAction.slash(name: String, description: String) = addCommands(Command(name, description) {})
fun SlashCommandData.subcommand(name: String, description: String) = addSubcommands(Subcommand(name, description) {})
fun SubcommandGroupData.subcommand(name: String, description: String) = addSubcommands(Subcommand(name, description) {})
fun SlashCommandData.group(name: String, description: String) = addSubcommandGroups(SubcommandGroup(name, description) {})

inline fun <reified T> SlashCommandData.option(
    name: String, description: String,
    required: Boolean = false, autocomplete: Boolean = false
) = addOptions(Option<T>(name, description, required, autocomplete) {})
inline fun <reified T> SubcommandData.option(
    name: String, description: String,
    required: Boolean = false, autocomplete: Boolean = false,
) = addOptions(Option<T>(name, description, required, autocomplete) {})

fun OptionData.choice(name: String, value: String) = addChoice(name, value)
fun OptionData.choice(name: String, value: Long) = addChoice(name, value)
fun OptionData.choice(name: String, value: Double) = addChoice(name, value)

inline fun JDA.updateCommands(builder: CommandListUpdateAction.() -> Unit) = updateCommands().apply(builder)
inline fun JDA.upsertCommand(name: String, description: String, builder: SlashCommandData.() -> Unit) = upsertCommand(Command(name, description, builder))
inline fun Guild.updateCommands(builder: CommandListUpdateAction.() -> Unit) = updateCommands().apply(builder)
inline fun Guild.upsertCommand(name: String, description: String, builder: SlashCommandData.() -> Unit) = upsertCommand(Command(name, description, builder))


inline fun Command.editCommand(builder: SlashCommandData.() -> Unit) = editCommand().apply(Commands.slash(name, description).apply(builder))

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
