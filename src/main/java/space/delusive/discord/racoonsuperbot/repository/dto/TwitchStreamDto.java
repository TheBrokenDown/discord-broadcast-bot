package space.delusive.discord.racoonsuperbot.repository.dto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class TwitchStreamDto {
    @NonNull
    private String streamId;
    @NonNull
    private String userId;
    @NonNull
    private String userName;
    @NonNull
    private String title;
}
