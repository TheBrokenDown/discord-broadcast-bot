package space.delusive.discord.racoonsuperbot.repository.impl;

import lombok.AllArgsConstructor;
import space.delusive.discord.racoonsuperbot.repository.YoutubeVideoRepository;

@AllArgsConstructor
public class YoutubeVideoRepositoryImpl implements YoutubeVideoRepository {
    private final String apiToken;

    @Override
    public String getIdOfLastUploadedVideo(String channelId) {

        return null;
    }
}
