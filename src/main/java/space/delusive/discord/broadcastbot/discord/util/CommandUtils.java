package space.delusive.discord.broadcastbot.discord.util;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import space.delusive.discord.broadcastbot.exception.MoreThanOneMentionedRoleFoundException;
import space.delusive.discord.broadcastbot.exception.NoMentionedRolesFoundException;
import space.delusive.discord.broadcastbot.util.Constants;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandUtils {
    /**
     * Predicate which will be true if the message was sent from the same text channel, message has same author
     * and the message is another.
     *
     * @param commandEvent event of a command (argument of an execute method)
     * @return predicate which was described above
     */
    public static Predicate<GuildMessageReceivedEvent> getSameAuthorSameChannelDifferentMessagesPredicate(CommandEvent commandEvent) {
        return event ->
                event.getChannel().equals(commandEvent.getChannel()) &&
                        event.getAuthor().equals(commandEvent.getAuthor()) &&
                        !event.getMessage().equals(commandEvent.getMessage());
    }

    /**
     * Extract a mentioned role id from the message
     *
     * @param message Message from which a mentioned role id should be extracted
     * @return id of a mentioned role
     * @throws NoMentionedRolesFoundException         if there are no mentioned roles in a message
     * @throws MoreThanOneMentionedRoleFoundException if a message contains more than one mentioned roles
     */
    public static String getMentionedRoleIdFrom(Message message) throws NoMentionedRolesFoundException, MoreThanOneMentionedRoleFoundException {
        String messageText = message.getContentRaw().trim();
        List<String> mentionedRolesId = message.getMentionedRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        if (messageText.contains(Constants.EVERYONE_ROLE_NAME_IN_DB)) {
            mentionedRolesId.add(Constants.EVERYONE_ROLE_NAME_IN_DB);
        }
        if (messageText.contains(Constants.NOBODY_ROLE_NAME_IN_DB)) {
            mentionedRolesId.add(Constants.NOBODY_ROLE_NAME_IN_DB);
        }
        if (mentionedRolesId.isEmpty()) {
            throw new NoMentionedRolesFoundException();
        }
        if (mentionedRolesId.size() > 1) {
            throw new MoreThanOneMentionedRoleFoundException();
        }
        return mentionedRolesId.get(0);
    }
}
