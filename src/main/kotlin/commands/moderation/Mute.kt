package commands.moderation

import commandhandler.CommandContext
import commands.BaseCommand
import commands.CommandTypes.Moderation
import database.muteRole
import ext.sendMuteLog
import ext.useArguments
import ext.useCommandProperly
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.requests.ErrorResponse

class Mute : BaseCommand(
    commandName = "mute",
    commandDescription = "Mute a member",
    commandType = Moderation
) {

    override fun execute(ctx: CommandContext) {
        super.execute(ctx)
        val args = ctx.args
        if (args.isNotEmpty()) {
            val user = args[0]
            val role = ctx.guild.getRoleById(guildId.muteRole)
            if (role != null) {
                addRole(args, user, role, ctx)
            } else {
                ctx.guild.createRole().setName("HelperMute").queue({
                    addRole(args, user, it, ctx)
                }, ErrorHandler().handle(ErrorResponse.MAX_ROLES_PER_GUILD) {
                    channel.sendMessage("Guild reached maximum amount of roles!").queue {
                        messageId = it.id
                    }
                })
            }
        } else {
            channel.useArguments(1, this)
        }
    }

    private fun addRole(args: MutableList<String>, user: String, role: Role, ctx: CommandContext) {
        val reason = if (args.size > 1) args.apply { remove(user) }.joinToString(" ") else "no reason provided"
        val id = user.filter { it.isDigit() }
        if (contentIDRegex.matchEntire(id)?.value?.matches(contentIDRegex) == true) {
            ctx.guild.retrieveMemberById(id).queue({ member ->
                ctx.guild.addRoleToMember(member, role).queue {
                    channel.sendMessage("Successfully muted ${member.asMention}").queue {
                        messageId = it.id
                    }
                    ctx.authorAsMember?.let { it1 -> embedBuilder.sendMuteLog(member, it1, reason, guildId) }
                }
            }, ErrorHandler().handle(ErrorResponse.UNKNOWN_USER) {
                channel.sendMessage("Provided user does not exist!").queue {
                    messageId = it.id
                }
            })
        } else {
            channel.useCommandProperly(this)
        }
    }

}