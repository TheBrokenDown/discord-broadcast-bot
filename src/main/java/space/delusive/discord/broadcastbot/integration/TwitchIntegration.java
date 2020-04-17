package space.delusive.discord.broadcastbot.integration;

import space.delusive.discord.broadcastbot.integration.dto.TwitchStreamDto;

public interface TwitchIntegration {
    TwitchStreamDto getCurrentStream(String userName);
}
