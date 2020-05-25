package space.delusive.discord.broadcastbot.service.impl;

import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.discord.DiscordManager;
import space.delusive.discord.broadcastbot.domain.YoutubeChannel;
import space.delusive.discord.broadcastbot.domain.YoutubeVideo;
import space.delusive.discord.broadcastbot.exception.ChannelNotFoundException;
import space.delusive.discord.broadcastbot.exception.YoutubeVideoNotFoundException;
import space.delusive.discord.broadcastbot.integration.YoutubeIntegration;
import space.delusive.discord.broadcastbot.integration.dto.YoutubeChannelInfoDto;
import space.delusive.discord.broadcastbot.integration.dto.YoutubeVideoDto;
import space.delusive.discord.broadcastbot.repository.YoutubeChannelRepository;
import space.delusive.discord.broadcastbot.repository.YoutubeVideoRepository;
import space.delusive.discord.broadcastbot.service.YoutubeService;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class YoutubeServiceImpl implements YoutubeService, Runnable {
    private final YoutubeChannelRepository youtubeChannelRepository;
    private final YoutubeVideoRepository youtubeVideoRepository;
    private final YoutubeIntegration youtubeIntegration;
    private final DiscordManager discordManager;

    @Scheduled(fixedDelay = 300000) // run every 5 minutes
    public void run() {
        Iterable<YoutubeChannel> channels = youtubeChannelRepository.findAll();
        channels.forEach(youtubeChannel -> {
            Optional<YoutubeVideo> newVideo = getNewVideo(youtubeChannel.getUploadsPlaylistId());
            newVideo.ifPresent(youtubeVideo ->
                discordManager.informAboutNewVideoOnYoutube(youtubeChannel, youtubeVideo));
        });
    }

    private Optional<YoutubeVideo> getNewVideo(String playlistId) {
        val youtubeVideoDto = youtubeIntegration.getLastUploadedVideoByPlaylistId(playlistId);
        val lastYoutubeVideoId = youtubeVideoDto.getItems().get(0).getSnippet().getResourceId().getVideoId();
        val optionalVideoFromDb = youtubeVideoRepository.getByYoutubeId(lastYoutubeVideoId);
        if (optionalVideoFromDb.isEmpty()) { // we don't know about last uploaded video so it is new
            YoutubeVideo youtubeVideo = new YoutubeVideo(lastYoutubeVideoId);
            youtubeVideoRepository.save(youtubeVideo);
            return Optional.of(youtubeVideo);
        }
        return Optional.empty();
    }

    @Override
    public List<YoutubeChannel> getAllChannels() {
        return youtubeChannelRepository.findAll();
    }

    @Override
    public YoutubeChannel getYoutubeChannelInfoByVideoId(String videoId)
            throws YoutubeVideoNotFoundException, ChannelNotFoundException {
        YoutubeVideoDto videoDto = youtubeIntegration.getVideoById(videoId);
        if (videoDto.getItems().isEmpty()) {
            throw new YoutubeVideoNotFoundException();
        }
        val snippet = videoDto.getItems().get(0).getSnippet();
        YoutubeChannelInfoDto channelInfo = youtubeIntegration.getChannelInfoById(snippet.getChannelId());
        if (channelInfo.getItems().isEmpty()) {
            throw new ChannelNotFoundException();
        }
        String uploadsPlaylistId = channelInfo.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads();
        YoutubeChannel youtubeChannel = new YoutubeChannel();
        youtubeChannel.setChannelName(snippet.getChannelTitle());
        youtubeChannel.setChannelId(snippet.getChannelId());
        youtubeChannel.setUploadsPlaylistId(uploadsPlaylistId);
        // mention role id field is empty, it will be filled in in another place
        return youtubeChannel;
    }

    @Override
    public Optional<YoutubeChannel> getChannelByName(String channelName) {
        return youtubeChannelRepository.getByChannelName(channelName);
    }

    @Override
    public void addChannel(YoutubeChannel youtubeChannel) {
        youtubeChannelRepository.save(youtubeChannel);
    }
}
