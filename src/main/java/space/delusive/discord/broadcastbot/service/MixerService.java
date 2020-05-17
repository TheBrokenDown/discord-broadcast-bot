package space.delusive.discord.broadcastbot.service;

import space.delusive.discord.broadcastbot.domain.MixerChannel;

import java.util.List;

public interface MixerService {
    List<MixerChannel> getAllChannels();
}
