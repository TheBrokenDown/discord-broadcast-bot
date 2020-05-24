package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.util.CommandUtils;
import space.delusive.discord.broadcastbot.exception.MoreThanOneMentionedRoleFoundException;
import space.delusive.discord.broadcastbot.exception.NoMentionedRolesFoundException;
import space.delusive.discord.broadcastbot.service.TwitchService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Log4j2
@Component
public class AddTwitchChannelCommand extends Command {
    private final TwitchService twitchService;
    private final EventWaiter eventWaiter;

    private Map<String, String> messages;

    public AddTwitchChannelCommand(TwitchService twitchService,
                                   EventWaiter eventWaiter,
                                   @Value("${discord.bot.command.add.twitch.channel.name}") String name,
                                   @Value("${discord.bot.command.add.twitch.channel.help}") String help,
                                   @Value("${discord.bot.command.add.twitch.channel.aliases}") String[] aliases) {
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.twitchService = twitchService;
        this.eventWaiter = eventWaiter;
        super.name = name;
        super.help = help;
        super.aliases = aliases;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(messages.get("add.twitch.channel.enter.name"));
        Runnable timeoutAction = () -> event.replyWarning(messages.get("add.twitch.channel.timeout"));
        eventWaiter.waitForEvent(GuildMessageReceivedEvent.class,
                CommandUtils.getSameAuthorSameChannelDifferentMessagesPredicate(event),
                getChannelNameReceivedConsumer(event), 1, TimeUnit.MINUTES, timeoutAction);
    }

    private Consumer<GuildMessageReceivedEvent> getChannelNameReceivedConsumer(CommandEvent commandEvent) {
        return channelNameMessageEvent -> {
            String messageText = channelNameMessageEvent.getMessage().getContentRaw();
            boolean isChannelNonexistent = !twitchService.checkChannelExistence(messageText);
            if (isChannelNonexistent) {
                commandEvent.replyError(messages.get("add.twitch.channel.invalid.name"));
                return;
            }
            val isChannelAlreadyExist = twitchService.getChannelByName(messageText).isPresent();
            if (isChannelAlreadyExist) {
                commandEvent.replyError(messages.get("add.twitch.channel.already.exist"));
                return;
            }
            Runnable timeoutAction = () -> commandEvent.replyWarning(messages.get("add.twitch.channel.timeout"));
            commandEvent.reply(messages.get("add.twitch.channel.enter.role"));
            eventWaiter.waitForEvent(GuildMessageReceivedEvent.class,
                    CommandUtils.getSameAuthorSameChannelDifferentMessagesPredicate(commandEvent),
                    getMentionRoleReceivedConsumer(commandEvent, messageText), 1, TimeUnit.MINUTES, timeoutAction);
        };
    }

    private Consumer<GuildMessageReceivedEvent> getMentionRoleReceivedConsumer(CommandEvent commandEvent,
                                                                               String channelName) {
        return mentionRoleMessageEvent -> {
            String mentionedRole = null;
            try {
                mentionedRole = CommandUtils.getMentionedRoleIdFrom(mentionRoleMessageEvent.getMessage());
            } catch (NoMentionedRolesFoundException e) {
                log.debug(e);
                commandEvent.replyError(messages.get("add.twitch.channel.enter.role.not.specified"));
            } catch (MoreThanOneMentionedRoleFoundException e) {
                log.debug(e);
                commandEvent.replyError(messages.get("add.twitch.channel.enter.role.more.than.one"));
            }
            if (mentionedRole == null) {
                return;
            }
            twitchService.addChannel(channelName, mentionedRole);
            commandEvent.replySuccess(messages.get("add.twitch.channel.success"));
        };
    }

    @Resource(name = "messages")
    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}
