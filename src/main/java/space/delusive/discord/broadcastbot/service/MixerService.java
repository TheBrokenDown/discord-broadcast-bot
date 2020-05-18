package space.delusive.discord.broadcastbot.service;

import space.delusive.discord.broadcastbot.domain.MixerChannel;
import space.delusive.discord.broadcastbot.exception.ChannelNotFoundException;
import space.delusive.discord.broadcastbot.util.Constants;

import java.util.List;
import java.util.Optional;

public interface MixerService {
    List<MixerChannel> getAllChannels();

    /**
     * Add the channel to the database
     *
     * @param channelName the name of the channel
     * @param mentionRole <i>ID of mention role</i> / <i>"everyone"</i> / <i>"nobody"</i> {@link Constants}
     * @throws ChannelNotFoundException if the channel with specified name doesn't exist
     */
    void addChannel(String channelName, String mentionRole) throws ChannelNotFoundException;

    /**
     * Check channel existence on Mixer
     *
     * @param channelName the name of the channel
     * @return <b>true</b> if the channel exists, <b>false</b> otherwise
     */
    boolean checkChannelExistence(String channelName);

    /**
     * Get the Mixer channel by name
     *
     * @param name the name of the channel
     * @return empty optional if the channel doesn't exist, wrapped into the optional channel otherwise
     */
    Optional<MixerChannel> getChannelByName(String name);
}
