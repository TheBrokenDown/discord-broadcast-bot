package space.delusive.discord.broadcastbot.service;

import space.delusive.discord.broadcastbot.domain.TwitchChannel;
import space.delusive.discord.broadcastbot.exception.ChannelNotFoundException;
import space.delusive.discord.broadcastbot.util.Constants;

import java.util.List;
import java.util.Optional;

public interface TwitchService {
    List<TwitchChannel> getAllChannels();

    /**
     * Add the channel to the database
     *
     * @param channelName the name of the channel
     * @param mentionRole <i>ID of mention role</i> / <i>"everyone"</i> / <i>"nobody"</i> {@link Constants}
     * @throws ChannelNotFoundException if channel doesn't exist
     */
    void addChannel(String channelName, String mentionRole) throws ChannelNotFoundException;

    /**
     * Check channel existence on Twitch
     *
     * @param channelName the name of the channel
     * @return <b>true</b> if the channel exists, <b>false</b> otherwise
     */
    boolean checkChannelExistence(String channelName);

    /**
     * Get the Twitch channel by name from the database
     *
     * @param channelName the name of the channel
     * @return empty optional if there is no such channel, wrapped into the optional channel object otherwise
     */
    Optional<TwitchChannel> getChannelByName(String channelName);
}
