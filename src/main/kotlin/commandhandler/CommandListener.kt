package commandhandler

import database.prefix
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandListener : ListenerAdapter() {

    private val commandManager = CommandManager()

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot || event.isWebhookMessage) {
            return
        }
        if (event.message.contentRaw.startsWith(event.guild.id.prefix)) {
            commandManager.execute(event)
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.user?.isBot == true) {
            return
        }
        commandManager.onReactionAdd(event)
    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        if (event.user?.isBot == true) {
            return
        }
        commandManager.onReactionRemove(event)
    }
}