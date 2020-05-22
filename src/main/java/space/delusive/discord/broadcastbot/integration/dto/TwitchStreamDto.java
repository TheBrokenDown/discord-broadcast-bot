package space.delusive.discord.broadcastbot.integration.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class TwitchStreamDto {
    @NonNull String streamId;
    @NonNull String userId;
    @NonNull String userName;
    @NonNull String title;
}
