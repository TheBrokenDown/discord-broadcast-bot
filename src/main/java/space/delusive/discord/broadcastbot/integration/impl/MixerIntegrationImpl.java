package space.delusive.discord.broadcastbot.integration.impl;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import space.delusive.discord.broadcastbot.exception.ChannelNotFoundException;
import space.delusive.discord.broadcastbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.broadcastbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.broadcastbot.integration.MixerIntegration;
import space.delusive.discord.broadcastbot.integration.dto.MixerStreamDto;

@Log4j2
@AllArgsConstructor
public class MixerIntegrationImpl implements MixerIntegration {
    private static final int HTTP_NOT_FOUND = 404;

    private final String lastStreamUrl;
    private final String channelInfoByNameUrl;

    @Override
    public MixerStreamDto getCurrentStream(String channelId) {
        HttpResponse<JsonNode> response = Unirest.get(lastStreamUrl)
                .routeParam("channelId", channelId)
                .asJson();
        log.debug("Request to get current stream on Mixer channel with id \"{}\" has been sent. Following answer has been received. Status: \"{}\", Body: \"{}\"",
                channelId, response.getStatusText(), response.getBody().toString());
        checkResponseOfGettingCurrentStream(response);
        val jsonObject = response.getBody().getObject();
        return extractDtoFromJsonObject(jsonObject);
    }

    @Override
    public String getChannelIdByName(String channelName) {
        HttpResponse<JsonNode> response = Unirest.get(channelInfoByNameUrl)
                .routeParam("channelName", channelName)
                .asJson();
        log.debug("Request to get channel id by name \"{}\" on Mixer has been sent. Following answer has been received. Status: \"{}\", Body: \"{}\"",
                channelName, response.getStatusText(), response.getBody().toString());
        checkResponseOfGettingChannelId(response);
        return String.valueOf(response.getBody().getObject().getInt("id"));
    }

    private MixerStreamDto extractDtoFromJsonObject(JSONObject jsonObject) { // TODO: 12/23/2019 check does stream exist
        val channelId = String.valueOf(jsonObject.getInt("channelId"));
        val isTestStream = jsonObject.getBoolean("isTestStream");
        val isOnline = jsonObject.getBoolean("online");
        val mixerId = jsonObject.getString("id");
        return new MixerStreamDto(channelId, isTestStream, isOnline, mixerId);
    }

    private void checkResponseOfGettingCurrentStream(HttpResponse<JsonNode> response) {
        if (response.getStatus() == HTTP_NOT_FOUND) {
            throw new NoCurrentStreamFoundException();
        }
        checkResponseForSuccess(response);
    }

    private void checkResponseOfGettingChannelId(HttpResponse<JsonNode> response) {
        if (response.getStatus() == HTTP_NOT_FOUND) {
            throw new ChannelNotFoundException();
        }
        checkResponseForSuccess(response);
    }

    private void checkResponseForSuccess(HttpResponse<JsonNode> response) {
        if (response.isSuccess()) {
            return;
        }
        throw new UnsuccessfulRequestException(
                String.format("Response status code is not success. The code is %s and the body is %s",
                        response.getStatus(), response.getBody().toPrettyString()));
    }
}
