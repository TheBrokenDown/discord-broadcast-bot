package space.delusive.discord.racoonsuperbot.service;

import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.delusive.discord.racoonsuperbot.dao.TwitchChannelDao;
import space.delusive.discord.racoonsuperbot.dao.TwitchStreamDao;
import space.delusive.discord.racoonsuperbot.discord.DiscordManager;
import space.delusive.discord.racoonsuperbot.domain.TwitchStream;
import space.delusive.discord.racoonsuperbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.racoonsuperbot.repository.TwitchStreamRepository;
import space.delusive.discord.racoonsuperbot.repository.dto.TwitchStreamDto;

import java.util.Optional;

@Component
@AllArgsConstructor
public class TwitchService {
    private final TwitchStreamDao twitchStreamDao;
    private final TwitchChannelDao twitchChannelDao;
    private final TwitchStreamRepository twitchStreamRepository;
    private final DiscordManager discordManager;

    @Scheduled(fixedDelay = 10000)
    public void run() {
        val channels = twitchChannelDao.findAll();
        channels.forEach(twitchChannel -> {
            val currentStreamIfNew = getCurrentStreamIfNew(twitchChannel.getChannelName());
            currentStreamIfNew.ifPresent(twitchStream -> {
                discordManager.informAboutBeginningOfStream(twitchChannel, twitchStream);
            });
        });
    }

    private Optional<TwitchStream> getCurrentStreamIfNew(String userName) {
        TwitchStreamDto currentStream;
        try {
            currentStream = twitchStreamRepository.getCurrentStream(userName);
        } catch (NoCurrentStreamFoundException e) {
            return Optional.empty();
        }
        TwitchStream streamFromDb = twitchStreamDao.getByTwitchId(currentStream.getStreamId());
        if (streamFromDb == null) {
            TwitchStream twitchStream = new TwitchStream(currentStream.getStreamId(), currentStream.getTitle());
            twitchStreamDao.save(twitchStream);
            return Optional.of(twitchStream);
        }
        return Optional.empty();
    }

}
