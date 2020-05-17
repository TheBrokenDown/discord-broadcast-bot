package space.delusive.discord.broadcastbot.discord.util;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.util.Constants;

import javax.annotation.Resource;
import java.awt.*;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GetChannelListCommandHelper {
    private final EventWaiter eventWaiter;
    private JDA jda;

    @Resource(name = "messages")
    private Map<String, String> messages;

    public Paginator getPaginator(String[] textItems, String caption) {
        return new Paginator.Builder()
                .waitOnSinglePage(true)
                .setEventWaiter(eventWaiter)
                .setItemsPerPage(2)
                .setColumns(1)
                .setBulkSkipNumber(3)
                .setColor(Color.ORANGE)
                .addItems(textItems)
                .setText(caption)
                .build();
    }

    public String getRoleName(String roleId) {
        return switch (roleId) {
            case Constants.NOBODY_ROLE_NAME_IN_DB -> messages.get("get.channel.list.mention.role.nobody");
            case Constants.EVERYONE_ROLE_NAME_IN_DB -> messages.get("get.channel.list.mention.role.everyone");
            default -> {
                Role role = jda.getRoleById(roleId);
                yield role == null ?
                        messages.get("get.channel.list.mention.role.not.found") :
                        role.getName();
            }
        };
    }

    @Autowired
    public void setJda(JDA jda) {
        this.jda = jda;
    }
}
