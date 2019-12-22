package space.delusive.discord.racoonsuperbot.repository;

import space.delusive.discord.racoonsuperbot.repository.dto.TwitchStreamDto;

public interface TwitchStreamRepository {
    TwitchStreamDto getCurrentStream(String userName);
}
