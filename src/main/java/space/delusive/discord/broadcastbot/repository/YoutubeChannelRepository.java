package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.YoutubeChannel;

public interface YoutubeChannelRepository extends CrudRepository<YoutubeChannel, Integer> {
}
