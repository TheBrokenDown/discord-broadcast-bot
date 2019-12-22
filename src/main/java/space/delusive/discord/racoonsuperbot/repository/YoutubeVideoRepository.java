package space.delusive.discord.racoonsuperbot.repository;

import space.delusive.discord.racoonsuperbot.repository.dto.YoutubeVideoDto;

public interface YoutubeVideoRepository {
    YoutubeVideoDto getLastUploadedVideo(String channelId);
}
