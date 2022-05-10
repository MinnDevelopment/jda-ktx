/*
 * Copyright (c) 2022. Human Ardaki
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

package dev.minn.jda.ktx.filters

/**
 * thrown by providers when context dependent criteria is not met.
 * any thrower should explicitly state they may throw this message
 * @see [Throws]
 * @param msg thrower may choose to provide message further explaining why this exception is thrown in
 * @author Human Ardaki
 * @since 0.9.1-alpha.11
 */
class KTXConstrainViolation(msg: String? = null): RuntimeException(msg)