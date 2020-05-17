package space.delusive.discord.broadcastbot.service;

import space.delusive.discord.broadcastbot.domain.YoutubeChannel;

import java.util.List;

public interface YoutubeService {
    List<YoutubeChannel> getAllChannels();
}
