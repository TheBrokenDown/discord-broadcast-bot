package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.YoutubeChannel;

import java.util.List;
import java.util.Optional;

public interface YoutubeChannelRepository extends CrudRepository<YoutubeChannel, Integer> {
    List<YoutubeChannel> findAll();

    Optional<YoutubeChannel> getByChannelName(String channelName);
}
