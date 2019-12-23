package space.delusive.discord.racoonsuperbot.service;

import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.delusive.discord.racoonsuperbot.dao.MixerChannelDao;
import space.delusive.discord.racoonsuperbot.dao.MixerStreamDao;
import space.delusive.discord.racoonsuperbot.discord.DiscordManager;
import space.delusive.discord.racoonsuperbot.domain.MixerStream;
import space.delusive.discord.racoonsuperbot.repository.MixerStreamRepository;
import space.delusive.discord.racoonsuperbot.repository.dto.MixerStreamDto;

import java.util.Optional;

@Component
@AllArgsConstructor
public class MixerService {
    private final MixerStreamDao mixerStreamDao;
    private final MixerChannelDao mixerChannelDao;
    private final MixerStreamRepository mixerStreamRepository;
    private final DiscordManager discordManager;

    @Scheduled(fixedDelay = 10000)
    public void run() {
        val channels = mixerChannelDao.findAll();
        channels.forEach(mixerChannel -> {
            val currentStreamIfNew = getCurrentStreamIfNew(mixerChannel.getChannelId());
            currentStreamIfNew.ifPresent(mixerStream -> {
                discordManager.informAboutBeginningOfStreamOnMixer(mixerChannel, mixerStream);
            });
        });
    }

    private Optional<MixerStream> getCurrentStreamIfNew(String channelId) {
        MixerStreamDto currentStream = mixerStreamRepository.getCurrentStream(channelId);
        if (currentStream.isTestStream() || isNotOnline(currentStream)) {
            return Optional.empty();
        }
        val streamFromDb = mixerStreamDao.getByMixerId(currentStream.getId());
        if (streamFromDb == null) {
            val mixerStream = new MixerStream(currentStream.getId());
            mixerStreamDao.save(mixerStream);
            return Optional.of(mixerStream);
        }
        return Optional.empty();
    }

    private boolean isNotOnline(MixerStreamDto mixerStreamDto) {
        return !mixerStreamDto.isOnline();
    }

}
