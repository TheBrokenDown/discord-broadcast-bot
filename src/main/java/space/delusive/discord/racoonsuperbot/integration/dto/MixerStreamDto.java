package space.delusive.discord.racoonsuperbot.integration.dto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class MixerStreamDto {
    @NonNull
    String channelId;
    @NonNull
    boolean isTestStream;
    @NonNull
    boolean isOnline;
    @NonNull
    String id;
}
