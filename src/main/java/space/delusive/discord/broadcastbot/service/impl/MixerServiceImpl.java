package space.delusive.discord.broadcastbot.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import space.delusive.discord.broadcastbot.discord.DiscordManager;
import space.delusive.discord.broadcastbot.domain.MixerChannel;
import space.delusive.discord.broadcastbot.domain.MixerStream;
import space.delusive.discord.broadcastbot.exception.ChannelNotFoundException;
import space.delusive.discord.broadcastbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.broadcastbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.broadcastbot.integration.MixerIntegration;
import space.delusive.discord.broadcastbot.integration.dto.MixerStreamDto;
import space.delusive.discord.broadcastbot.repository.MixerChannelRepository;
import space.delusive.discord.broadcastbot.repository.MixerStreamRepository;
import space.delusive.discord.broadcastbot.service.MixerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
@AllArgsConstructor
public class MixerServiceImpl implements MixerService, Runnable {
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
            log.warn(e);
            return Optional.empty();
        } catch (NoCurrentStreamFoundException e) {
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

    @Override
    public List<MixerChannel> getAllChannels() {
        val channels = new ArrayList<MixerChannel>();
        mixerChannelRepository.findAll().forEach(channels::add);
        return channels;
    }

    @Override
    public void addChannel(String channelName, String mentionRole) throws ChannelNotFoundException {
        MixerChannel mixerChannel = new MixerChannel();
        mixerChannel.setChannelName(channelName);
        mixerChannel.setChannelId(mixerIntegration.getChannelIdByName(channelName));
        mixerChannel.setMentionRoleId(mentionRole);
        mixerChannelRepository.save(mixerChannel);
    }

    @Override
    public boolean checkChannelExistence(String channelName) {
        try {
            mixerIntegration.getChannelIdByName(channelName);
            return true;
        } catch (ChannelNotFoundException e) {
            log.debug(e);
        }
        return false;
    }

    @Override
    public Optional<MixerChannel> getChannelByName(String name) {
        return mixerChannelRepository.getByChannelName(name);
    }

    @Override
    @Transactional
    public void removeChannelByName(String channelName) {
        mixerChannelRepository.removeByChannelName(channelName);
    }
}
