package space.delusive.discord.racoonsuperbot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import space.delusive.discord.racoonsuperbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.racoonsuperbot.repository.TwitchStreamRepository;
import space.delusive.discord.racoonsuperbot.repository.dto.TwitchStreamDto;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TwitchIntegrationTests {
    @Autowired
    private TwitchStreamRepository twitchStreamRepository;

    @Test
    @DisplayName("Get current stream from twitch")
    public void getTwitchStreamFromWebService(@Value("starladder_cs_en") String userName) {
        try {
            TwitchStreamDto currentStream = twitchStreamRepository.getCurrentStream(userName);
            assertTrue(currentStream.getUserName().equalsIgnoreCase(userName));
        } catch (NoCurrentStreamFoundException e) {
            assertTrue(true);
        }
    }

}
