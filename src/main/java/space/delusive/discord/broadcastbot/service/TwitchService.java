package space.delusive.discord.broadcastbot.service;

import space.delusive.discord.broadcastbot.domain.TwitchChannel;

import java.util.List;

public interface TwitchService {
    List<TwitchChannel> getAllChannels();
}
