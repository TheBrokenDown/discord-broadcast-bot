package space.delusive.discord.racoonsuperbot.discord;

import space.delusive.discord.racoonsuperbot.domain.TwitchChannel;
import space.delusive.discord.racoonsuperbot.domain.TwitchStream;
import space.delusive.discord.racoonsuperbot.domain.YoutubeChannel;
import space.delusive.discord.racoonsuperbot.domain.YoutubeVideo;

public interface DiscordManager {
    void informAboutNewVideoOnYoutube(YoutubeChannel youtubeChannel, YoutubeVideo youtubeVideo);

    void informAboutBeginningOfStream(TwitchChannel twitchChannel, TwitchStream twitchStream);
}
