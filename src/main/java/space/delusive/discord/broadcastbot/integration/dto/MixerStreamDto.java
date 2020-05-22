package space.delusive.discord.broadcastbot.integration.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class MixerStreamDto {
    @NonNull String channelId;
    @NonNull boolean isTestStream;
    @NonNull boolean isOnline;
    @NonNull String id;
}
