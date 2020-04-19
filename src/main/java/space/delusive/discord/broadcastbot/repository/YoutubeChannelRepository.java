package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.YoutubeChannel;

import java.util.List;

public interface YoutubeChannelRepository extends CrudRepository<YoutubeChannel, Integer> {
    List<YoutubeChannel> findAll();
}
