package space.delusive.discord.racoonsuperbot.repository;

import space.delusive.discord.racoonsuperbot.repository.dto.MixerStreamDto;

public interface MixerStreamRepository {
    MixerStreamDto getCurrentStream(String channelId);
}
