package space.delusive.discord.broadcastbot.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.delusive.discord.broadcastbot.integration.MixerIntegration;
import space.delusive.discord.broadcastbot.repository.MixerChannelRepository;
import space.delusive.discord.broadcastbot.repository.MixerStreamRepository;
import space.delusive.discord.broadcastbot.discord.DiscordManager;
import space.delusive.discord.broadcastbot.domain.MixerStream;
import space.delusive.discord.broadcastbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.broadcastbot.integration.dto.MixerStreamDto;

import java.util.Optional;

@Component
@Log4j2
@AllArgsConstructor
public class MixerService {
    private final MixerChannelRepository mixerChannelRepository;
    private final MixerStreamRepository mixerStreamRepository;
    private final MixerIntegration mixerIntegration;
    private final DiscordManager discordManager;

    @Scheduled(fixedDelay = 10000)
    public void run() {
        val channels = mixerChannelRepository.findAll();
        channels.forEach(mixerChannel -> {
            val currentStreamIfNew = getCurrentStreamIfNew(mixerChannel.getChannelId());
            currentStreamIfNew.ifPresent(mixerStream ->
                discordManager.informAboutBeginningOfStreamOnMixer(mixerChannel, mixerStream));
        });
    }

    private Optional<MixerStream> getCurrentStreamIfNew(String channelId) {
        MixerStreamDto currentStream;
        try {
            currentStream = mixerIntegration.getCurrentStream(channelId);
        } catch (UnsuccessfulRequestException e) {
            log.debug(e);
            return Optional.empty();
        }
        if (currentStream.isTestStream() || isNotOnline(currentStream)) {
            return Optional.empty();
        }
        val optionalStreamFromDb = mixerStreamRepository.getByMixerId(currentStream.getId());
        if (optionalStreamFromDb.isEmpty()) {
            val mixerStream = new MixerStream(currentStream.getId());
            mixerStreamRepository.save(mixerStream);
            return Optional.of(mixerStream);
        }
        return Optional.empty();
    }

    private boolean isNotOnline(MixerStreamDto mixerStreamDto) {
        return !mixerStreamDto.isOnline();
    }

}
