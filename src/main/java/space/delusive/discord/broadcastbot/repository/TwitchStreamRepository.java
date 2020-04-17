package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.TwitchStream;

import java.util.Optional;

public interface TwitchStreamRepository extends CrudRepository<TwitchStream, Integer> {
    Optional<TwitchStream> getByTwitchId(String twitchId);
}
