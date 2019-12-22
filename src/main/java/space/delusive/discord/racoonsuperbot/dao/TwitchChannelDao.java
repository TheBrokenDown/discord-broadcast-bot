package space.delusive.discord.racoonsuperbot.dao;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.TwitchChannel;

public interface TwitchChannelDao extends CrudRepository<TwitchChannel, Integer> {
}
