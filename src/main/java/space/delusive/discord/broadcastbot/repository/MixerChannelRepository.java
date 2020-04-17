package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.MixerChannel;

public interface MixerChannelRepository extends CrudRepository<MixerChannel, Integer> {
}
