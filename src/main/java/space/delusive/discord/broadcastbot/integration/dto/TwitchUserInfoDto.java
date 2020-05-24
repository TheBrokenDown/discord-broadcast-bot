package space.delusive.discord.broadcastbot.integration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.List;

@Value
public class TwitchUserInfoDto {
    List<Data> data;

    @Value
    public class Data {
        @SerializedName("id") String id;
        @SerializedName("login") String login;
        @SerializedName("display_name") String displayName;
        @SerializedName("type") String type;
        @SerializedName("broadcaster_type") String broadcasterType;
        @SerializedName("description") String description;
        @SerializedName("profile_image_url") String profileImageUrl;
        @SerializedName("offline_image_url") String offlineImageUrl;
        @SerializedName("view_count") long viewCount;
    }
}
