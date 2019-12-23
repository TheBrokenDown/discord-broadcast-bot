package space.delusive.discord.racoonsuperbot.discord;

import space.delusive.discord.racoonsuperbot.domain.*;

public interface DiscordManager {
    void informAboutNewVideoOnYoutube(YoutubeChannel youtubeChannel, YoutubeVideo youtubeVideo);

    void informAboutBeginningOfStreamOnTwitch(TwitchChannel twitchChannel, TwitchStream twitchStream);

    void informAboutBeginningOfStreamOnMixer(MixerChannel mixerChannel, MixerStream mixerStream);
}
