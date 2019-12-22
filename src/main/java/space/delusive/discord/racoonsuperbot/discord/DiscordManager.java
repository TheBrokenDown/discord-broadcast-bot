package space.delusive.discord.racoonsuperbot.discord;

import space.delusive.discord.racoonsuperbot.domain.YoutubeChannel;
import space.delusive.discord.racoonsuperbot.domain.YoutubeVideo;

public interface DiscordManager {
    void informAboutNewVideoOnYoutube(YoutubeChannel youtubeChannel, YoutubeVideo youtubeVideo);
}
