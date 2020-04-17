package space.delusive.discord.broadcastbot.service;

import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.integration.YoutubeIntegration;
import space.delusive.discord.broadcastbot.repository.YoutubeChannelRepository;
import space.delusive.discord.broadcastbot.repository.YoutubeVideoRepository;
import space.delusive.discord.broadcastbot.discord.DiscordManager;
import space.delusive.discord.broadcastbot.domain.YoutubeChannel;
import space.delusive.discord.broadcastbot.domain.YoutubeVideo;
import space.delusive.discord.broadcastbot.integration.dto.YoutubeVideoDto;

import java.util.Optional;

@Component
@AllArgsConstructor
public class YoutubeService {
    private final YoutubeChannelRepository youtubeChannelRepository;
    private final YoutubeVideoRepository youtubeVideoRepository;
    private final YoutubeIntegration youtubeIntegration;
    private final DiscordManager discordManager;

    @Scheduled(fixedDelay = 130000) // run every 130 seconds
    public void run() {
        Iterable<YoutubeChannel> channels = youtubeChannelRepository.findAll();
        channels.forEach(youtubeChannel -> {
            Optional<YoutubeVideo> newVideo = getNewVideo(youtubeChannel.getUploadsPlaylistId());
            newVideo.ifPresent(youtubeVideo ->
                discordManager.informAboutNewVideoOnYoutube(youtubeChannel, youtubeVideo));
        });
    }

    private Optional<YoutubeVideo> getNewVideo(String playlistId) {
        YoutubeVideoDto lastUploadedVideo = youtubeIntegration.getLastUploadedVideoByPlaylistId(playlistId);
        val optionalVideoFromDb = youtubeVideoRepository.getByYoutubeId(lastUploadedVideo.getVideoId());
        if (optionalVideoFromDb.isEmpty()) { //we don't know about last uploaded video so it is new
            YoutubeVideo youtubeVideo = new YoutubeVideo(lastUploadedVideo.getVideoId());
            youtubeVideoRepository.save(youtubeVideo);
            return Optional.of(youtubeVideo);
        }
        return Optional.empty();
    }
}
