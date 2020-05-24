package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.TwitchChannel;

import java.util.Optional;

public interface TwitchChannelRepository extends CrudRepository<TwitchChannel, Integer> {
    Optional<TwitchChannel> getByChannelName(String channelName);

    void removeByChannelName(String channelName);
}
