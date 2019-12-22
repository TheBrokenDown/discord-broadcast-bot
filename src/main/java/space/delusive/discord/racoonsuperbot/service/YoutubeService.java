package space.delusive.discord.racoonsuperbot.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import space.delusive.discord.racoonsuperbot.dao.YoutubeVideoDao;
import space.delusive.discord.racoonsuperbot.discord.DiscordManager;
import space.delusive.discord.racoonsuperbot.repository.YoutubeVideoRepository;

@Component
@AllArgsConstructor
public class YoutubeService extends Thread {
    private final YoutubeVideoDao youtubeVideoDao;
    private final YoutubeVideoRepository youtubeVideoRepository;
    private final DiscordManager discordManager;

    @Override
    public void run() {

    }

    private boolean isNewVideoUploaded() {
        // TODO: 12/22/2019
        return false;
    }
}
