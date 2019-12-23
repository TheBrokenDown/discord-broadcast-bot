package space.delusive.discord.racoonsuperbot.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.delusive.discord.racoonsuperbot.dao.YoutubeChannelDao;
import space.delusive.discord.racoonsuperbot.dao.YoutubeVideoDao;
import space.delusive.discord.racoonsuperbot.discord.DiscordManager;
import space.delusive.discord.racoonsuperbot.domain.YoutubeChannel;
import space.delusive.discord.racoonsuperbot.domain.YoutubeVideo;
import space.delusive.discord.racoonsuperbot.repository.YoutubeVideoRepository;
import space.delusive.discord.racoonsuperbot.repository.dto.YoutubeVideoDto;

import java.util.Optional;

@Component
@AllArgsConstructor
public class YoutubeService {
    private final YoutubeVideoDao youtubeVideoDao;
    private final YoutubeChannelDao youtubeChannelDao;
    private final YoutubeVideoRepository youtubeVideoRepository;
    private final DiscordManager discordManager;

    @Scheduled(fixedDelay = 30000) // run every 30 seconds
    public void run() {
        Iterable<YoutubeChannel> channels = youtubeChannelDao.findAll();
        channels.forEach(youtubeChannel -> {
            Optional<YoutubeVideo> newVideo = getNewVideo(youtubeChannel.getUploadsPlaylistId());
            newVideo.ifPresent(youtubeVideo -> {
                discordManager.informAboutNewVideoOnYoutube(youtubeChannel, youtubeVideo);
            });
        });
    }

    private Optional<YoutubeVideo> getNewVideo(String playlistId) {
        YoutubeVideoDto lastUploadedVideo = youtubeVideoRepository.getLastUploadedVideoByPlaylistId(playlistId);
        YoutubeVideo videoFromDb = youtubeVideoDao.getByYoutubeId(lastUploadedVideo.getVideoId());
        if (videoFromDb == null) { //we don't know about last uploaded video so it is new
            YoutubeVideo youtubeVideo = new YoutubeVideo(lastUploadedVideo.getVideoId());
            youtubeVideoDao.save(youtubeVideo);
            return Optional.of(youtubeVideo);
        }
        return Optional.empty();
    }
}
