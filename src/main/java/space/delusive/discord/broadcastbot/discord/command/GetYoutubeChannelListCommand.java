package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.util.GetChannelListCommandHelper;
import space.delusive.discord.broadcastbot.domain.YoutubeChannel;
import space.delusive.discord.broadcastbot.repository.YoutubeChannelRepository;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class GetYoutubeChannelListCommand extends Command {
    @Resource(name = "messages")
    private Map<String, String> messages;

    private final YoutubeChannelRepository youtubeChannelRepository;
    private final GetChannelListCommandHelper helper;


    public GetYoutubeChannelListCommand(YoutubeChannelRepository youtubeChannelRepository,
                                        GetChannelListCommandHelper helper,
                                        @Value("${discord.bot.command.get.youtube.channel.list.name}") String name,
                                        @Value("${discord.bot.command.get.youtube.channel.list.help}") String help,
                                        @Value("${discord.bot.command.get.youtube.channel.list.aliases}") String[] aliases) {
        this.youtubeChannelRepository = youtubeChannelRepository;
        this.helper = helper;
        super.name = name;
        super.help = help;
        super.aliases = aliases;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] textItems = youtubeChannelRepository.findAll().stream()
                .map(this::getFormattedMessage)
                .toArray(String[]::new);
        String caption = messages.get("get.youtube.channel.list.title.message");
        helper.getPaginator(textItems, caption).display(event.getChannel());
    }

    private String getFormattedMessage(YoutubeChannel channel) {
        String roleName = helper.getRoleName(channel.getMentionRoleId());
        return messages.get("get.youtube.channel.list.message.pattern")
                .replaceAll("\\{channel_name}", channel.getChannelName())
                .replaceAll("\\{channel_id}", channel.getChannelId())
                .replaceAll("\\{mention_role}", roleName)
                .replaceAll("\\{uploads_playlist_id}", channel.getUploadsPlaylistId());
    }
}
