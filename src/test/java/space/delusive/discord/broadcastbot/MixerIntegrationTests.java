package space.delusive.discord.broadcastbot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import space.delusive.discord.broadcastbot.exception.ChannelNotFoundException;
import space.delusive.discord.broadcastbot.integration.MixerIntegration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MixerIntegrationTests {
    @Autowired
    private MixerIntegration mixerIntegration;

    @Test
    @DisplayName("Get channel id by channel name")
    public void getChannelIdByName(@Value("jond") String channelName,
                                   @Value("1191171") String expectedChannelId) {
        String actualChannelId = mixerIntegration.getChannelIdByName(channelName);
        assertEquals(expectedChannelId, actualChannelId);
    }

    @Test
    @DisplayName("Throw an exception if channel doesn't exist")
    public void getIdOfNonexistentChannel(@Value("channelWithThatNameDoesntExist") String nameOfNonexistentChannel) {
        assertThrows(ChannelNotFoundException.class, () ->
                mixerIntegration.getChannelIdByName(nameOfNonexistentChannel));
    }
}
