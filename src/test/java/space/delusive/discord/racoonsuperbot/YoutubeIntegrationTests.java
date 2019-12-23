package space.delusive.discord.racoonsuperbot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import space.delusive.discord.racoonsuperbot.repository.YoutubeVideoRepository;
import space.delusive.discord.racoonsuperbot.repository.dto.YoutubeVideoDto;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class YoutubeIntegrationTests {
    @Autowired
    private YoutubeVideoRepository youtubeVideoRepository;

    @Test
    @DisplayName("Get some video from youtube")
    public void getYoutubeVideoFromWebService(@Value("UUQ5zOV00L1ESumw4qJADULA") String playlistId) {
        YoutubeVideoDto youtubeVideo = youtubeVideoRepository.getLastUploadedVideoByPlaylistId(playlistId);
        assertFalse(youtubeVideo.getChannelTitle().isBlank());
    }

}
