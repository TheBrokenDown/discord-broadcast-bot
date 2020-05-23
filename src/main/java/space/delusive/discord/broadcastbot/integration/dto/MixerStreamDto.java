package space.delusive.discord.broadcastbot.integration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class MixerStreamDto {
    @SerializedName("channelId") String channelId;
    @SerializedName("isTestStream") boolean isTestStream;
    @SerializedName("online") boolean isOnline;
    @SerializedName("id") String id;
}
