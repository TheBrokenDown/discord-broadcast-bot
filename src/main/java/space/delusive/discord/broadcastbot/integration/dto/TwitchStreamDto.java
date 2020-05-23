package space.delusive.discord.broadcastbot.integration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class TwitchStreamDto {
    @SerializedName("id") String streamId;
    @SerializedName("user_id") String userId;
    @SerializedName("user_name") String userName;
    @SerializedName("title") String title;
}
