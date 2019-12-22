package space.delusive.discord.racoonsuperbot.dao;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.TwitchStream;

public interface TwitchStreamDao extends CrudRepository<TwitchStream, Integer> {
    TwitchStream getByTwitchId(String twitchId);
}
