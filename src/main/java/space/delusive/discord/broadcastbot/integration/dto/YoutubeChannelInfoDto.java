package space.delusive.discord.broadcastbot.integration.dto;

import lombok.Value;

import java.util.List;

@Value
public class YoutubeChannelInfoDto {
    String kind;
    String etag;
    PageInfo pageInfo;
    List<Item> items;

    @Value
    public class PageInfo {
        int totalResults;
        int resultsPerPage;
    }

    @Value
    public class Item {
        String kind;
        String etag;
        String id;
        ContentDetails contentDetails;

        @Value
        public class ContentDetails {
            RelatedPlaylists relatedPlaylists;

            @Value
            public class RelatedPlaylists {
                String likes;
                String favourites;
                String uploads;
                String watchHistory;
                String watchLater;
            }
        }
    }
}
