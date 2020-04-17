package space.delusive.discord.racoonsuperbot.integration;

import space.delusive.discord.racoonsuperbot.integration.dto.TwitchStreamDto;

public interface TwitchIntegration {
    TwitchStreamDto getCurrentStream(String userName);
}
