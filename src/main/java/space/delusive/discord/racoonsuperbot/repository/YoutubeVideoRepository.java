package space.delusive.discord.racoonsuperbot.repository;

import space.delusive.discord.racoonsuperbot.repository.dto.YoutubeVideoDto;

public interface YoutubeVideoRepository {
    @Deprecated
    YoutubeVideoDto getLastUploadedVideoByChannelId(String channelId);

    YoutubeVideoDto getLastUploadedVideoByPlaylistId(String playlistId);
}
