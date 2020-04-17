package space.delusive.discord.racoonsuperbot.integration.dto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class TwitchStreamDto {
    @NonNull
    String streamId;
    @NonNull
    String userId;
    @NonNull
    String userName;
    @NonNull
    String title;
}
