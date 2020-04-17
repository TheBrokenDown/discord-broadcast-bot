package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.YoutubeVideo;

import java.util.Optional;

public interface YoutubeVideoRepository extends CrudRepository<YoutubeVideo, Integer> {
    Optional<YoutubeVideo> getByYoutubeId(String youtubeId);
}
