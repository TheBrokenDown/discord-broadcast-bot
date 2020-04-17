package space.delusive.discord.racoonsuperbot.integration.impl;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import space.delusive.discord.racoonsuperbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.racoonsuperbot.integration.YoutubeIntegration;
import space.delusive.discord.racoonsuperbot.integration.dto.YoutubeVideoDto;

import java.time.LocalDateTime;

@Log4j2
@AllArgsConstructor
public class YoutubeIntegrationImpl implements YoutubeIntegration {
    private final String searchVideosByChannelIdUrl;
    private final String getVideosFromPlaylistUrl;
    private final String apiToken;

    @Override
    public YoutubeVideoDto getLastUploadedVideoByChannelId(String channelId) throws UnsuccessfulRequestException {
        HttpResponse<JsonNode> response = Unirest.get(searchVideosByChannelIdUrl)
                .routeParam("channelId", channelId)
                .routeParam("apiToken", apiToken)
                .asJson();
        checkResponseForSuccess(response);
        val jsonObject = (JSONObject) response.getBody().getObject().getJSONArray("items").get(0); // TODO: 12/22/2019 review case if there is no videos available
        return extractVideoDtoFromJsonObjectByChannelId(jsonObject);
    }

    @Override
    public YoutubeVideoDto getLastUploadedVideoByPlaylistId(String playlistId) {
        HttpResponse<JsonNode> response = Unirest.get(getVideosFromPlaylistUrl)
                .routeParam("playlistId", playlistId)
                .routeParam("apiToken", apiToken)
                .asJson();
        checkResponseForSuccess(response);
        val jsonObject = (JSONObject) response.getBody().getObject().getJSONArray("items").get(0); // TODO: 12/23/2019 here too :p
        return extractVideoDtoFromJsonObjectByPlaylistId(jsonObject);
    }

    private YoutubeVideoDto extractVideoDtoFromJsonObjectByChannelId(JSONObject jsonObject) {
        val videoId = jsonObject.getJSONObject("id").getString("videoId");
        val snippet = jsonObject.getJSONObject("snippet");
        return generateDtoFromSnippetAndId(videoId, snippet);
    }

    private YoutubeVideoDto extractVideoDtoFromJsonObjectByPlaylistId(JSONObject jsonObject) {
        val snippet = jsonObject.getJSONObject("snippet");
        val videoId = snippet.getJSONObject("resourceId").getString("videoId");
        return generateDtoFromSnippetAndId(videoId, snippet);
    }

    private YoutubeVideoDto generateDtoFromSnippetAndId(String id, JSONObject snippet) {
        val publishTime = LocalDateTime.parse(snippet.getString("publishedAt").replaceFirst(".$", ""));
        val title = snippet.getString("title");
        val description = snippet.getString("description");
        val channelTitle = snippet.getString("channelTitle");
        return new YoutubeVideoDto(publishTime, title, description, id, channelTitle);
    }

    private void checkResponseForSuccess(HttpResponse<JsonNode> response) throws UnsuccessfulRequestException {
        if (isNotSuccess(response)) {
            throw new UnsuccessfulRequestException(
                    String.format("Response status code is not success. The code is %s and the body is %s",
                            response.getStatus(), response.getBody().toPrettyString()));
        }
    }

    private boolean isNotSuccess(HttpResponse response) {
        return !response.isSuccess();
    }
}
