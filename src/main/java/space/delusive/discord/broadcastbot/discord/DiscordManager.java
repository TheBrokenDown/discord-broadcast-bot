package space.delusive.discord.broadcastbot.discord;

import space.delusive.discord.broadcastbot.domain.*;

public interface DiscordManager {
    void informAboutNewVideoOnYoutube(YoutubeChannel youtubeChannel, YoutubeVideo youtubeVideo);

    void informAboutBeginningOfStreamOnTwitch(TwitchChannel twitchChannel, TwitchStream twitchStream);

    void informAboutBeginningOfStreamOnMixer(MixerChannel mixerChannel, MixerStream mixerStream);
}
