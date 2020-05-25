package space.delusive.discord.broadcastbot.integration.impl;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import space.delusive.discord.broadcastbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.broadcastbot.integration.YoutubeIntegration;
import space.delusive.discord.broadcastbot.integration.dto.YoutubeChannelInfoDto;
import space.delusive.discord.broadcastbot.integration.dto.YoutubeVideoDto;

@Log4j2
@AllArgsConstructor
public class YoutubeIntegrationImpl implements YoutubeIntegration {
    private final String getVideosFromPlaylistUrl;
    private final String getChannelInfoById;
    private final String getVideoById;
    private final String apiToken;
    private final Gson gson;

    @Override
    public YoutubeVideoDto getLastUploadedVideoByPlaylistId(String playlistId) {
        HttpResponse<JsonNode> response = Unirest.get(getVideosFromPlaylistUrl)
                .routeParam("playlistId", playlistId)
                .routeParam("apiToken", apiToken)
                .asJson();
        log.debug("Request to get last uploaded video on YouTube by playlist id \"{}\" has been sent. " +
                        "Following answer has been received. Status: \"{}\" Body: \"{}\"",
                playlistId, response.getStatusText(), response.getBody().toString());
        checkResponseForSuccess(response);
        return gson.fromJson(response.getBody().toString(), YoutubeVideoDto.class);
    }

    @Override
    public YoutubeVideoDto getVideoById(String videoId) {
        HttpResponse<JsonNode> response = Unirest.get(getVideoById)
                .routeParam("videoId", videoId)
                .routeParam("apiToken", apiToken)
                .asJson();
        log.debug("Request to get video by id \"{}\" from YouTube has been sent. " +
                        "Following answer has been received. Status: \"{}\" Body: \"{}\"",
                videoId, response.getStatusText(), response.getBody().toString());
        checkResponseForSuccess(response);
        return gson.fromJson(response.getBody().toString(), YoutubeVideoDto.class);
    }

    @Override
    public YoutubeChannelInfoDto getChannelInfoById(String channelId) {
        HttpResponse<JsonNode> response = Unirest.get(getChannelInfoById)
                .routeParam("channelId", channelId)
                .routeParam("apiToken", apiToken)
                .asJson();
        checkResponseForSuccess(response);
        return gson.fromJson(response.getBody().toString(), YoutubeChannelInfoDto.class);
    }

    private void checkResponseForSuccess(HttpResponse<JsonNode> response) throws UnsuccessfulRequestException {
        if (isNotSuccess(response)) {
            throw new UnsuccessfulRequestException(
                    String.format("Response status code is not success. The code is %s and the body is %s",
                            response.getStatus(), response.getBody().toPrettyString()));
        }
    }

    private boolean isNotSuccess(HttpResponse<?> response) {
        return !response.isSuccess();
    }
}
