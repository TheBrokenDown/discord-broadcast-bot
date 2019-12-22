package space.delusive.discord.racoonsuperbot.repository.impl;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import space.delusive.discord.racoonsuperbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.racoonsuperbot.repository.YoutubeVideoRepository;
import space.delusive.discord.racoonsuperbot.repository.dto.YoutubeVideoDto;

import java.time.LocalDateTime;

@Log4j2
@AllArgsConstructor
public class YoutubeVideoRepositoryImpl implements YoutubeVideoRepository {
    private final String url;
    private final String apiToken;

    @Override
    public YoutubeVideoDto getLastUploadedVideo(String channelId) throws UnsuccessfulRequestException {
        HttpResponse<JsonNode> response = Unirest.get(url)
                .routeParam("channelId", channelId)
                .routeParam("apiToken", apiToken)
                .asJson();
        if (isNotSuccess(response)) {
            throw new UnsuccessfulRequestException(
                    String.format("Response status code is not success. The code is %s and the body is %s",
                            response.getStatus(), response.getBody().toPrettyString()));
        }
        val jsonObject = (JSONObject) response.getBody().getObject().getJSONArray("items").get(0); // TODO: 12/22/2019 review case if there is no videos available
        val snippet = jsonObject.getJSONObject("snippet");

        val videoId = jsonObject.getJSONObject("id").getString("videoId");
        val publishTime = LocalDateTime.parse(snippet.getString("publishedAt").replaceFirst(".$", ""));
        val title = snippet.getString("title");
        val description = snippet.getString("description");
        val channelTitle = snippet.getString("channelTitle");
        return new YoutubeVideoDto(publishTime, title, description, videoId, channelTitle);
    }

    private boolean isNotSuccess(HttpResponse response) {
        return !response.isSuccess();
    }
}
