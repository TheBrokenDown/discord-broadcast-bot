package space.delusive.discord.broadcastbot.integration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.List;

@Value
public class YoutubeVideoDto {
    String kind;
    String etag;
    String nextPageToken;
    List<PlaylistItem> items;

    @Value
    public class PlaylistItem {
        String kind;
        String etag;
        String id;
        Snippet snippet;

        @Value
        public class Snippet {
            String publishedAt;
            String channelId;
            String title;
            String description;
            Thumbnails thumbnails;
            String channelTitle;
            String playlistId;
            int position;
            ResourceId resourceId;

            @Value
            public class Thumbnails {
                // "default" cannot be used because it is reserved word
                @SerializedName("default") Thumbnail defaultThumbnail;
                @SerializedName("medium") Thumbnail mediumThumbnail;
                @SerializedName("high") Thumbnail highThumbnail;
                @SerializedName("standard") Thumbnail standardThumbnail;
                @SerializedName("maxres") Thumbnail maxresThumbnail;

                @Value
                public class Thumbnail {
                    String url;
                    int width;
                    int height;
                }
            }

            @Value
            public class ResourceId {
                String kind;
                String videoId;
            }
        }
    }
}
