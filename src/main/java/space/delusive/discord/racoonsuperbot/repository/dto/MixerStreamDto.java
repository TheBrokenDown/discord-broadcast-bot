package space.delusive.discord.racoonsuperbot.repository.dto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class MixerStreamDto {
    @NonNull
    private String channelId;
    @NonNull
    private boolean isTestStream;
    @NonNull
    private boolean isOnline;
    @NonNull
    private String id;
}
