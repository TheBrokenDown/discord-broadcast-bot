package space.delusive.discord.racoonsuperbot.repository.dto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
public class YoutubeVideoDto {
    @NonNull
    private final LocalDateTime publishedAt;
    @NonNull
    private final String title;
    @NonNull
    private final String description;
    @NonNull
    private final String videoId;
    @NonNull
    private final String channelTitle;
}
