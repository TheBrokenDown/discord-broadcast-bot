package space.delusive.discord.racoonsuperbot.repository.impl;

import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import space.delusive.discord.racoonsuperbot.repository.YoutubeVideoRepository;
import space.delusive.discord.racoonsuperbot.repository.dto.YoutubeVideoDto;

import java.time.LocalDateTime;

@Log4j2
@AllArgsConstructor
public class YoutubeVideoRepositoryImpl implements YoutubeVideoRepository {
    private final String url;
    private final String apiToken;

    @Override
    public YoutubeVideoDto getLastUploadedVideo(String channelId) {
        Object firstItem = Unirest.get(url)
                .routeParam("channelId", channelId)
                .routeParam("apiToken", apiToken)
                .asJson().getBody().getObject().getJSONArray("items").get(0);
        val jsonObject = (JSONObject) firstItem;
        val snippet = jsonObject.getJSONObject("snippet");

        val videoId = jsonObject.getJSONObject("id").getString("videoId");
        val publishTime = LocalDateTime.parse(snippet.getString("publishedAt").replaceFirst(".$", ""));
        val title = snippet.getString("title");
        val description = snippet.getString("description");
        val channelTitle = snippet.getString("channelTitle");
        return new YoutubeVideoDto(publishTime, title, description, videoId, channelTitle);
    }
}
