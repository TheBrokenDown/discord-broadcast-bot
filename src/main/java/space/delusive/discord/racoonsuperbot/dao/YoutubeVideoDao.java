package space.delusive.discord.racoonsuperbot.dao;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.YoutubeVideo;

public interface YoutubeVideoDao extends CrudRepository<YoutubeVideo, Integer> {
    YoutubeVideo getByYoutubeId(String youtubeId);
}
