package space.delusive.discord.racoonsuperbot.discord.impl;

import org.springframework.stereotype.Component;
import space.delusive.discord.racoonsuperbot.discord.DiscordManager;

@Component
public class DiscordManagerImpl implements DiscordManager {
    private String botToken;
    private String channelId;
}
