package space.delusive.discord.racoonsuperbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.MixerStream;

import java.util.Optional;

public interface MixerStreamRepository extends CrudRepository<MixerStream, Integer> {
    Optional<MixerStream> getByMixerId(String mixerId);
}
