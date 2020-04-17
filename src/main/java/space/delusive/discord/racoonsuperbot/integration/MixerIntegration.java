package space.delusive.discord.racoonsuperbot.integration;

import space.delusive.discord.racoonsuperbot.integration.dto.MixerStreamDto;

public interface MixerIntegration {
    MixerStreamDto getCurrentStream(String channelId);
}
