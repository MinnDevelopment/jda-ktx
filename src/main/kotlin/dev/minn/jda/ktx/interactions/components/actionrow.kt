package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.entities.emoji.Emoji

private val DUMMY_ROW = ActionRow.of(Button.success("id", "label"))

class InlineActionRow : InlineComponent {

    private var row = DUMMY_ROW

    override var uniqueId: Int
        get() = row.uniqueId
        set(value) {
            row = row.withUniqueId(value)
        }

    val components = mutableListOf<ActionRowChildComponent>()

    fun button(
        style: ButtonStyle,
        customId: String,
        label: String? = ButtonDefaults.LABEL,
        emoji: Emoji? = ButtonDefaults.EMOJI,
        disabled: Boolean = ButtonDefaults.DISABLED,
        uniqueId: Int = -1,
    ) {
        components += Button.of(style, customId, label, emoji)
            .withUniqueId(uniqueId)
            .withDisabled(disabled)
    }

    fun primaryButton(
        customId: String,
        label: String? = ButtonDefaults.LABEL,
        emoji: Emoji? = ButtonDefaults.EMOJI,
        disabled: Boolean = ButtonDefaults.DISABLED,
        uniqueId: Int = -1,
    ) {
        components += primary(customId, label, emoji, uniqueId, disabled)
    }

    fun secondaryButton(
        customId: String,
        label: String? = ButtonDefaults.LABEL,
        emoji: Emoji? = ButtonDefaults.EMOJI,
        disabled: Boolean = ButtonDefaults.DISABLED,
        uniqueId: Int = -1,
    ) {
        components += secondary(customId, label, emoji, uniqueId, disabled)
    }

    fun successButton(
        customId: String,
        label: String? = ButtonDefaults.LABEL,
        emoji: Emoji? = ButtonDefaults.EMOJI,
        disabled: Boolean = ButtonDefaults.DISABLED,
        uniqueId: Int = -1,
    ) {
        components += success(customId, label, emoji, uniqueId, disabled)
    }

    fun dangerButton(
        customId: String,
        label: String? = ButtonDefaults.LABEL,
        emoji: Emoji? = ButtonDefaults.EMOJI,
        disabled: Boolean = ButtonDefaults.DISABLED,
        uniqueId: Int = -1,
    ) {
        components += danger(customId, label, emoji, uniqueId, disabled)
    }

    fun linkButton(
        url: String,
        label: String? = ButtonDefaults.LABEL,
        emoji: Emoji? = ButtonDefaults.EMOJI,
        disabled: Boolean = ButtonDefaults.DISABLED,
        uniqueId: Int = -1,
    ) {
        components += link(url, label, emoji, uniqueId, disabled)
    }

    inline fun stringSelectMenu(
        customId: String,
        uniqueId: Int = -1,
        placeholder: String? = null,
        valueRange: IntRange = 1..1,
        disabled: Boolean = false,
        options: Collection<SelectOption> = emptyList(),
        builder: StringSelectMenu.Builder.() -> Unit = {},
    ) {
        components += StringSelectMenu(customId, uniqueId, placeholder, valueRange, disabled, options, builder)
    }

    inline fun entitySelectMenu(
        customId: String,
        types: Collection<SelectTarget>,
        uniqueId: Int = -1,
        placeholder: String? = null,
        valueRange: IntRange = 1..1,
        disabled: Boolean = false,
        builder: EntitySelectMenu.Builder.() -> Unit = {},
    ) {
        components += EntitySelectMenu(customId, types, uniqueId, placeholder, valueRange, disabled, builder)
    }

    fun build(): ActionRow {
        return row.withComponents(components)
    }
}

/**
 * See [ActionRow][net.dv8tion.jda.api.components.actionrow.ActionRow].
 *
 * @param uniqueId    Unique identifier of this component
 * @param block       Lambda allowing further configuration
 *
 * @see ActionRowChildComponent
 */
inline fun ActionRow(uniqueId: Int = -1, block: InlineActionRow.() -> Unit): ActionRow =
    InlineActionRow()
        .apply {
            if (uniqueId != -1)
                this.uniqueId = uniqueId
            block()
        }
        .build()
