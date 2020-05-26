package space.delusive.discord.broadcastbot.service;

import space.delusive.discord.broadcastbot.domain.YoutubeChannel;
import space.delusive.discord.broadcastbot.exception.ChannelNotFoundException;
import space.delusive.discord.broadcastbot.exception.YoutubeVideoNotFoundException;

import java.util.List;
import java.util.Optional;

public interface YoutubeService {
    List<YoutubeChannel> getAllChannels();

    /**
     * Get channel info from YouTube by video id
     *
     * @param videoId id of any public video
     * @return information about channel which has uploaded this video
     * @throws YoutubeVideoNotFoundException If there is no such video
     * @throws ChannelNotFoundException      That is unusual case. We found the video but didn't find channel which owns
     *                                       it (is that possible? o_o)
     */
    YoutubeChannel getYoutubeChannelInfoByVideoId(String videoId)
            throws YoutubeVideoNotFoundException, ChannelNotFoundException;

    /**
     * Get channel with specified name from the database
     *
     * @param channelName the name of the channel
     * @return requested channel if it was found, empty optional otherwise
     */
    Optional<YoutubeChannel> getChannelByName(String channelName);

    /**
     * Add the channel to the database
     *
     * @param youtubeChannel target channel
     */
    void addChannel(YoutubeChannel youtubeChannel);

    /**
     * Get channel from the database by channelId field (not id!)
     *
     * @param channelId id of the channel
     * @return requested channel if it was found, empty optional otherwise
     */
    Optional<YoutubeChannel> getChannelByChannelId(String channelId);

    /**
     * Remove the channel from the database
     *
     * @param channelId id of the removable channel
     */
    void removeChannelById(String channelId);
}
