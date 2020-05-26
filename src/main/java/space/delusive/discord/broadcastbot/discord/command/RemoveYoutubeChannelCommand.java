package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.util.CommandUtils;
import space.delusive.discord.broadcastbot.service.YoutubeService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class RemoveYoutubeChannelCommand extends Command {
    private final YoutubeService youtubeService;
    private final EventWaiter eventWaiter;

    private Map<String, String> messages;

    public RemoveYoutubeChannelCommand(YoutubeService youtubeService,
                                      EventWaiter eventWaiter,
                                      @Value("${discord.bot.command.remove.youtube.channel.name}") String name,
                                      @Value("${discord.bot.command.remove.youtube.channel.help}") String help,
                                      @Value("${discord.bot.command.remove.youtube.channel.aliases}") String[] aliases) {
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.youtubeService = youtubeService;
        this.eventWaiter = eventWaiter;
        super.name = name;
        super.help = help;
        super.aliases = aliases;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(messages.get("remove.youtube.channel.enter.name"));
        Runnable timeoutAction = () -> event.replyWarning(messages.get("remove.youtube.channel.timeout"));
        eventWaiter.waitForEvent(GuildMessageReceivedEvent.class,
                CommandUtils.getSameAuthorSameChannelDifferentMessagesPredicate(event),
                getChannelNameReceivedConsumer(event), 1, TimeUnit.MINUTES, timeoutAction);
    }

    private Consumer<GuildMessageReceivedEvent> getChannelNameReceivedConsumer(CommandEvent event) {
        return guildMessageReceivedEvent -> {
            String messageText = guildMessageReceivedEvent.getMessage().getContentRaw();
            boolean isChannelNonexistent = youtubeService.getChannelByChannelId(messageText).isEmpty();
            if (isChannelNonexistent) {
                event.replyError(messages.get("remove.youtube.channel.not.found"));
                return;
            }
            youtubeService.removeChannelById(messageText);
            event.replySuccess(messages.get("remove.youtube.channel.success"));
        };
    }

    @Resource(name = "messages")
    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}
