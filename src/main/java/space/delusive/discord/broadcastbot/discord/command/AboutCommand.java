package space.delusive.discord.broadcastbot.discord.command;

import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class AboutCommand extends com.jagrosh.jdautilities.examples.command.AboutCommand {
    public AboutCommand(@Value("${discord.bot.command.about.description}") String description,
                        @Value("${discord.bot.command.about.features}") String[] features) {
        super(Color.ORANGE, description, features, Permission.ADMINISTRATOR);
    }
}
