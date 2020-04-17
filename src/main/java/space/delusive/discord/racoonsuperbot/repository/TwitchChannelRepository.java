package space.delusive.discord.racoonsuperbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.TwitchChannel;

public interface TwitchChannelRepository extends CrudRepository<TwitchChannel, Integer> {
}
