package space.delusive.discord.broadcastbot.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.domain.YoutubeChannel;
import space.delusive.discord.broadcastbot.repository.YoutubeChannelRepository;

import javax.annotation.Resource;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GetYoutubeChannelListCommand extends Command {
    private static final String NOBODY_ROLE_NAME = "noone";
    private static final String EVERYONE_ROLE_NAME = "everyone";

    @Resource(name = "messages")
    private Map<String, String> messages;

    @Autowired // because of cyclic dependence between JDA and this bean
    private JDA jda;

    private final YoutubeChannelRepository youtubeChannelRepository;
    private final EventWaiter eventWaiter;


    public GetYoutubeChannelListCommand(YoutubeChannelRepository youtubeChannelRepository,
                                        EventWaiter eventWaiter,
                                        @Value("${discord.bot.command.get.youtube.channel.list.name}") String name,
                                        @Value("${discord.bot.command.get.youtube.channel.list.help}") String help,
                                        @Value("${discord.bot.command.get.youtube.channel.list.aliases}") String[] aliases) {
        this.youtubeChannelRepository = youtubeChannelRepository;
        this.eventWaiter = eventWaiter;
        super.name = name;
        super.help = help;
        super.aliases = aliases;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] textItems = youtubeChannelRepository.findAll().stream()
                .map(this::getFormattedMessage)
                .toArray(String[]::new);
        new Paginator.Builder()
                .waitOnSinglePage(true)
                .setEventWaiter(eventWaiter)
                .setItemsPerPage(2)
                .setColumns(1)
                .setBulkSkipNumber(3)
                .setColor(Color.ORANGE)
                .addItems(textItems)
                .setText(messages.get("get.youtube.channel.list.title.message"))
                .build().display(event.getChannel());
    }

    private String getFormattedMessage(YoutubeChannel channel) {
        String roleName;
        switch (channel.getMentionRoleId()) {
            case NOBODY_ROLE_NAME:
                roleName = messages.get("get.youtube.channel.list.channel.mention.role.nobody");
                break;
            case EVERYONE_ROLE_NAME:
                roleName = messages.get("get.youtube.channel.list.channel.mention.role.everyone");
                break;
            default:
                Role role = jda.getRoleById(channel.getMentionRoleId());
                roleName = role == null ?
                        messages.get("get.youtube.channel.list.channel.mention.role.not.found") :
                        role.getName();
        }
        return messages.get("get.youtube.channel.list.message.pattern")
                .replaceAll("\\{channel_name}", channel.getChannelName())
                .replaceAll("\\{channel_id}", channel.getChannelId())
                .replaceAll("\\{mention_role}", roleName)
                .replaceAll("\\{uploads_playlist_id}", channel.getUploadsPlaylistId());
    }
}
