package space.delusive.discord.racoonsuperbot.dao;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.MixerStream;

public interface MixerStreamDao extends CrudRepository<MixerStream, Integer> {
    MixerStream getByMixerId(String mixerId);
}
