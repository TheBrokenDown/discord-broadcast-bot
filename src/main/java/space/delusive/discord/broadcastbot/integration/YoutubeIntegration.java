package space.delusive.discord.broadcastbot.integration;

import space.delusive.discord.broadcastbot.integration.dto.YoutubeVideoDto;

public interface YoutubeIntegration {
    @Deprecated
    YoutubeVideoDto getLastUploadedVideoByChannelId(String channelId);

    YoutubeVideoDto getLastUploadedVideoByPlaylistId(String playlistId);
}
