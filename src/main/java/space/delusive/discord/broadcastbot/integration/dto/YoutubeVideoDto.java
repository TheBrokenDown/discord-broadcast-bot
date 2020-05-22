package space.delusive.discord.broadcastbot.integration.dto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
public class YoutubeVideoDto {
    @NonNull LocalDateTime publishedAt;
    @NonNull String title;
    @NonNull String description;
    @NonNull String videoId;
    @NonNull String channelTitle;
}
