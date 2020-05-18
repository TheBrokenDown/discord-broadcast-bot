package space.delusive.discord.broadcastbot.repository;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.broadcastbot.domain.MixerChannel;

import java.util.Optional;

public interface MixerChannelRepository extends CrudRepository<MixerChannel, Integer> {
    Optional<MixerChannel> getByChannelName(String channelName);

    void removeByChannelName(String channelName);
}
