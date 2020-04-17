package space.delusive.discord.broadcastbot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import space.delusive.discord.broadcastbot.integration.YoutubeIntegration;
import space.delusive.discord.broadcastbot.integration.dto.YoutubeVideoDto;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class YoutubeIntegrationTests {
    @Autowired
    private YoutubeIntegration youtubeIntegration;

    @Test
    @DisplayName("Get some video from youtube")
    public void getYoutubeVideoFromWebService(@Value("UUQ5zOV00L1ESumw4qJADULA") String playlistId) {
        YoutubeVideoDto youtubeVideo = youtubeIntegration.getLastUploadedVideoByPlaylistId(playlistId);
        assertFalse(youtubeVideo.getChannelTitle().isBlank());
    }

}
