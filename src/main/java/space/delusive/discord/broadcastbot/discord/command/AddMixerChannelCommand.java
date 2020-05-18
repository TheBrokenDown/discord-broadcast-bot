package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.util.CommandUtils;
import space.delusive.discord.broadcastbot.exception.MoreThanOneMentionedRoleFoundException;
import space.delusive.discord.broadcastbot.exception.NoMentionedRolesFoundException;
import space.delusive.discord.broadcastbot.service.MixerService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.function.Consumer;

@Log4j2
@Component
public class AddMixerChannelCommand extends Command {
    @Resource(name = "messages")
    private Map<String, String> messages;

    private final MixerService mixerService;
    private final EventWaiter eventWaiter;

    public AddMixerChannelCommand(MixerService mixerService,
                                  EventWaiter eventWaiter,
                                  @Value("${discord.bot.command.add.mixer.channel.name}") String name,
                                  @Value("${discord.bot.command.add.mixer.channel.help}") String help,
                                  @Value("${discord.bot.command.add.mixer.channel.aliases}") String[] aliases) {
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.mixerService = mixerService;
        this.eventWaiter = eventWaiter;
        super.name = name;
        super.help = help;
        super.aliases = aliases;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(messages.get("add.mixer.channel.enter.name"));
        eventWaiter.waitForEvent(GuildMessageReceivedEvent.class,
                CommandUtils.getSameAuthorSameChannelDifferentMessagesPredicate(event),
                getChannelNameReceivedConsumer(event));
    }

    private Consumer<GuildMessageReceivedEvent> getChannelNameReceivedConsumer(CommandEvent event) {
        return channelNameMessageEvent -> {
            String messageText = channelNameMessageEvent.getMessage().getContentRaw();
            boolean doesChannelExist = mixerService.checkChannelExistence(messageText);
            if (!doesChannelExist) {
                event.replyError(messages.get("add.mixer.channel.invalid.name"));
                return;
            }
            boolean doesChannelAlreadyRegistered = mixerService.getChannelByName(messageText).isPresent();
            if (doesChannelAlreadyRegistered) {
                event.replyError(messages.get("add.mixer.channel.already.exist"));
                return;
            }
            event.reply(messages.get("add.mixer.channel.enter.role"));
            eventWaiter.waitForEvent(GuildMessageReceivedEvent.class, // wait for a message with role mention from the user
                    CommandUtils.getSameAuthorSameChannelDifferentMessagesPredicate(event),
                    getMentionRoleReceivedConsumer(event, messageText));
        };
    }

    private Consumer<GuildMessageReceivedEvent> getMentionRoleReceivedConsumer(CommandEvent event, String channelName) {
        return roleMentionMessageEvent -> {
            String mentionedRoleId = null;
            try {
                mentionedRoleId = CommandUtils.getMentionedRoleIdFrom(roleMentionMessageEvent.getMessage());
            } catch (NoMentionedRolesFoundException e) {
                log.debug(e);
                event.replyError(messages.get("add.mixer.channel.enter.role.not.specified"));
            } catch (MoreThanOneMentionedRoleFoundException e) {
                log.debug(e);
                event.replyError(messages.get("add.mixer.channel.enter.role.more.than.one"));
            }
            if (mentionedRoleId == null) {
                return;
            }
            mixerService.addChannel(channelName, mentionedRoleId);
            event.replySuccess(messages.get("add.mixer.channel.success"));
        };
    }
}
