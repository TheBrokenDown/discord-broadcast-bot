package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.util.GetChannelListCommandHelper;
import space.delusive.discord.broadcastbot.domain.MixerChannel;
import space.delusive.discord.broadcastbot.repository.MixerChannelRepository;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.StreamSupport;

@Component
public class GetMixerChannelListCommand extends Command {
    @Resource(name = "messages")
    private Map<String, String> messages;

    private final MixerChannelRepository channelRepository;
    private final GetChannelListCommandHelper helper;

    public GetMixerChannelListCommand(MixerChannelRepository channelRepository,
                                      GetChannelListCommandHelper helper,
                                      @Value("${discord.bot.command.get.mixer.channel.list.name}") String name,
                                      @Value("${discord.bot.command.get.mixer.channel.list.help}") String help,
                                      @Value("${discord.bot.command.get.mixer.channel.list.aliases}") String[] aliases) {
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
            event.reply(messages.get("get.mixer.channel.list.no.channels.found"));
        } else {
            String caption = messages.get("get.mixer.channel.list.title.message");
            helper.getPaginator(textItems, caption).display(event.getChannel());
        }
    }

    private String getFormattedMessage(MixerChannel channel) {
        String roleName = helper.getRoleName(channel.getMentionRoleId());
        return messages.get("get.mixer.channel.list.message.pattern")
                .replaceAll("\\{channel_name}", channel.getChannelName())
                .replaceAll("\\{channel_id}", channel.getChannelId())
                .replaceAll("\\{mention_role}", roleName);
    }
}
