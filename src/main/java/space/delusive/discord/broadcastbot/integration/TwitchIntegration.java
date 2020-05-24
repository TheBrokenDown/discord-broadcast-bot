package space.delusive.discord.broadcastbot.integration;

import space.delusive.discord.broadcastbot.integration.dto.TwitchStreamDto;
import space.delusive.discord.broadcastbot.integration.dto.TwitchUserInfoDto;

public interface TwitchIntegration {
    TwitchStreamDto getCurrentStream(String userName);

    TwitchUserInfoDto getUserInfo(String userName);
}
