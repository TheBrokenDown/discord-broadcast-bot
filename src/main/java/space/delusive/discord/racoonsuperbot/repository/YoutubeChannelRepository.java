package space.delusive.discord.racoonsuperbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.YoutubeChannel;

public interface YoutubeChannelRepository extends CrudRepository<YoutubeChannel, Integer> {
}
