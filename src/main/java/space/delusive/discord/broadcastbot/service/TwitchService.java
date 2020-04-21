package space.delusive.discord.broadcastbot.service;

import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.integration.TwitchIntegration;
import space.delusive.discord.broadcastbot.repository.TwitchChannelRepository;
import space.delusive.discord.broadcastbot.repository.TwitchStreamRepository;
import space.delusive.discord.broadcastbot.discord.DiscordManager;
import space.delusive.discord.broadcastbot.domain.TwitchStream;
import space.delusive.discord.broadcastbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.broadcastbot.integration.dto.TwitchStreamDto;

import java.util.Optional;

@Component
@AllArgsConstructor
public class TwitchService {
    private final TwitchStreamRepository twitchStreamRepository;
    private final TwitchChannelRepository twitchChannelRepository;
    private final TwitchIntegration twitchIntegration;
    private final DiscordManager discordManager;

    @Scheduled(fixedDelay = 60000)
    public void run() {
        val channels = twitchChannelRepository.findAll();
        channels.forEach(twitchChannel -> {
            val currentStreamIfNew = getCurrentStreamIfNew(twitchChannel.getChannelName());
            currentStreamIfNew.ifPresent(twitchStream ->
                discordManager.informAboutBeginningOfStreamOnTwitch(twitchChannel, twitchStream));
        });
    }

    private Optional<TwitchStream> getCurrentStreamIfNew(String userName) {
        TwitchStreamDto currentStream;
        try {
            currentStream = twitchIntegration.getCurrentStream(userName);
        } catch (NoCurrentStreamFoundException e) {
            return Optional.empty();
        }
        val optionalStreamFromDb = twitchStreamRepository.getByTwitchId(currentStream.getStreamId());
        if (optionalStreamFromDb.isEmpty()) {
            TwitchStream twitchStream = new TwitchStream(currentStream.getStreamId(), currentStream.getTitle());
            twitchStreamRepository.save(twitchStream);
            return Optional.of(twitchStream);
        }
        return Optional.empty();
    }

}
