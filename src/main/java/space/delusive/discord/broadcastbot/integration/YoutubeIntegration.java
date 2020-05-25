package space.delusive.discord.broadcastbot.integration;

import space.delusive.discord.broadcastbot.integration.dto.YoutubeChannelInfoDto;
import space.delusive.discord.broadcastbot.integration.dto.YoutubeVideoDto;

public interface YoutubeIntegration {
    YoutubeVideoDto getLastUploadedVideoByPlaylistId(String playlistId);

    YoutubeVideoDto getVideoById(String videoId);

    YoutubeChannelInfoDto getChannelInfoById(String channelId);
}
