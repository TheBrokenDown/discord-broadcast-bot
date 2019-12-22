package space.delusive.discord.racoonsuperbot.repository;

public interface YoutubeVideoRepository {
    String getIdOfLastUploadedVideo(String channelId);
}
