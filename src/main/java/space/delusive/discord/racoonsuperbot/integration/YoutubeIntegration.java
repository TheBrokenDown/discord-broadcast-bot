package space.delusive.discord.racoonsuperbot.integration;

import space.delusive.discord.racoonsuperbot.integration.dto.YoutubeVideoDto;

public interface YoutubeIntegration {
    @Deprecated
    YoutubeVideoDto getLastUploadedVideoByChannelId(String channelId);

    YoutubeVideoDto getLastUploadedVideoByPlaylistId(String playlistId);
}
