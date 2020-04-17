package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.MixerStream;

import java.util.Optional;

public interface MixerStreamRepository extends CrudRepository<MixerStream, Integer> {
    Optional<MixerStream> getByMixerId(String mixerId);
}
