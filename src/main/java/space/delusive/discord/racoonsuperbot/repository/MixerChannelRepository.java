package space.delusive.discord.racoonsuperbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.MixerChannel;

public interface MixerChannelRepository extends CrudRepository<MixerChannel, Integer> {
}
