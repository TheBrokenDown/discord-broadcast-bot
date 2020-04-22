package space.delusive.discord.broadcastbot.integration;

import space.delusive.discord.broadcastbot.integration.dto.MixerStreamDto;

public interface MixerIntegration {
    MixerStreamDto getCurrentStream(String channelId);

    String getChannelIdByName(String channelName);
}
