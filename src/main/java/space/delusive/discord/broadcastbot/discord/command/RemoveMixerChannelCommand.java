package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.util.CommandUtils;
import space.delusive.discord.broadcastbot.service.MixerService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class RemoveMixerChannelCommand extends Command {
    private final MixerService mixerService;
    private final EventWaiter eventWaiter;

    private Map<String, String> messages;

    public RemoveMixerChannelCommand(MixerService mixerService,
                                     EventWaiter eventWaiter,
                                     @Value("${discord.bot.command.remove.mixer.channel.name}") String name,
                                     @Value("${discord.bot.command.remove.mixer.channel.help}") String help,
                                     @Value("${discord.bot.command.remove.mixer.channel.aliases}") String[] aliases) {
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.mixerService = mixerService;
        this.eventWaiter = eventWaiter;
        super.name = name;
        super.help = help;
        super.aliases = aliases;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(messages.get("remove.mixer.channel.enter.name"));
        Runnable timeoutAction = () -> event.replyWarning(messages.get("remove.mixer.channel.timeout"));
        eventWaiter.waitForEvent(GuildMessageReceivedEvent.class,
                CommandUtils.getSameAuthorSameChannelDifferentMessagesPredicate(event),
                getChannelNameReceivedConsumer(event), 1, TimeUnit.MINUTES, timeoutAction);
    }

    private Consumer<GuildMessageReceivedEvent> getChannelNameReceivedConsumer(CommandEvent event) {
        return guildMessageReceivedEvent -> {
            String messageText = guildMessageReceivedEvent.getMessage().getContentRaw();
            boolean isChannelNonexistent = mixerService.getChannelByName(messageText).isEmpty();
            if (isChannelNonexistent) {
                event.replyError(messages.get("remove.mixer.channel.not.found"));
                return;
            }
            mixerService.removeChannelByName(messageText);
            event.replySuccess(messages.get("remove.mixer.channel.success"));
        };
    }

    @Resource(name = "messages")
    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}
