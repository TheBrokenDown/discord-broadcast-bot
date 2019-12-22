package space.delusive.discord.racoonsuperbot.dao;

import org.springframework.data.repository.CrudRepository;
import space.delusive.discord.racoonsuperbot.domain.YoutubeChannel;

public interface YoutubeChannelDao extends CrudRepository<YoutubeChannel, Integer> {
}
