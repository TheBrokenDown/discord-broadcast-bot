package space.delusive.discord.broadcastbot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import space.delusive.discord.broadcastbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.broadcastbot.integration.TwitchIntegration;
import space.delusive.discord.broadcastbot.integration.dto.TwitchStreamDto;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TwitchIntegrationTests {
    @Autowired
    private TwitchIntegration twitchIntegration;

    @Test
    @DisplayName("Get current stream from twitch")
    public void getTwitchStreamFromWebService(@Value("starladder_cs_en") String userName) {
        try {
            TwitchStreamDto currentStream = twitchIntegration.getCurrentStream(userName);
            assertTrue(currentStream.getUserName().equalsIgnoreCase(userName));
        } catch (NoCurrentStreamFoundException e) {
            assertTrue(true);
        }
    }

}
