package space.delusive.discord.racoonsuperbot.dao;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.MixerChannel;

public interface MixerChannelDao extends CrudRepository<MixerChannel, Integer> {
}
