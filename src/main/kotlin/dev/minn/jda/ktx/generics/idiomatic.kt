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

package dev.minn.jda.ktx.generics

import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer

/**
 * Same as [IGuildChannelContainer.getChannelById] but with a generic type parameter instead.
 */
inline fun <reified T : Channel> IGuildChannelContainer.getChannel(id: Long) = getChannelById(T::class.java, id)

/**
 * Same as [IGuildChannelContainer.getChannelById] but with a generic type parameter instead.
 */
inline fun <reified T : Channel> IGuildChannelContainer.getChannel(id: String) = getChannelById(T::class.java, id)

/**
 * Same as [IGuildChannelContainer.getChannelById] but with a generic type parameter instead.
 */
inline fun <reified T : Channel> IGuildChannelContainer.getChannel(id: ULong) = getChannel<T>(id.toLong())
