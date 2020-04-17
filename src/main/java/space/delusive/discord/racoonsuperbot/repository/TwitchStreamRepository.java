package space.delusive.discord.racoonsuperbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.TwitchStream;

import java.util.Optional;

public interface TwitchStreamRepository extends CrudRepository<TwitchStream, Integer> {
    Optional<TwitchStream> getByTwitchId(String twitchId);
}
