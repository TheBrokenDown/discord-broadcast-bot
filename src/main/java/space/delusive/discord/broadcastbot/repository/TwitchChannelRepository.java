package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.TwitchChannel;

public interface TwitchChannelRepository extends CrudRepository<TwitchChannel, Integer> {
}
