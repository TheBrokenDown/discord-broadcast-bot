package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.util.GetChannelListCommandHelper;
import space.delusive.discord.broadcastbot.domain.TwitchChannel;
import space.delusive.discord.broadcastbot.repository.TwitchChannelRepository;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.StreamSupport;

@Component
public class GetTwitchChannelListCommand extends Command {
    @Resource(name = "messages")
    private Map<String, String> messages;

    private final TwitchChannelRepository channelRepository;
    private final GetChannelListCommandHelper helper;

    public GetTwitchChannelListCommand(TwitchChannelRepository channelRepository,
                                       GetChannelListCommandHelper helper,
                                       @Value("${discord.bot.command.get.twitch.channel.list.name}") String name,
                                       @Value("${discord.bot.command.get.twitch.channel.list.help}") String help,
                                       @Value("${discord.bot.command.get.twitch.channel.list.aliases}") String[] aliases) {
        this.channelRepository = channelRepository;
        this.helper = helper;
        super.name = name;
        super.help = help;
        super.aliases = aliases;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] textItems = StreamSupport.stream(channelRepository.findAll().spliterator(), false)
                .map(this::getFormattedMessage)
                .toArray(String[]::new);
        if (textItems.length == 0) {
            event.reply(messages.get("get.twitch.channel.list.no.channels.found"));
        } else {
            String caption = messages.get("get.twitch.channel.list.title.message");
            helper.getPaginator(textItems, caption).display(event.getChannel());
        }
    }

    private String getFormattedMessage(TwitchChannel channel) {
        String roleName = helper.getRoleName(channel.getMentionRoleId());
        return messages.get("get.twitch.channel.list.message.pattern")
                .replaceAll("\\{channel_name}", channel.getChannelName())
                .replaceAll("\\{mention_role}", roleName);
    }
}
