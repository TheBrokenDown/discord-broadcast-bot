package space.delusive.discord.racoonsuperbot.integration.impl;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.val;
import space.delusive.discord.racoonsuperbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.racoonsuperbot.integration.MixerIntegration;
import space.delusive.discord.racoonsuperbot.integration.dto.MixerStreamDto;

@AllArgsConstructor
public class MixerIntegrationImpl implements MixerIntegration {
    private final String lastStreamUrl;

    @Override
    public MixerStreamDto getCurrentStream(String channelId) {
        HttpResponse<JsonNode> response = Unirest.get(lastStreamUrl)
                .routeParam("channelId", channelId)
                .asJson();
        if (isNotSuccess(response)) {
            throw new UnsuccessfulRequestException(
                    String.format("Response status code is not success. The code is %s and the body is %s",
                            response.getStatus(), response.getBody().toPrettyString()));
        }
        val jsonObject = response.getBody().getObject();
        return extractDtoFromJsonObject(jsonObject);
    }

    private MixerStreamDto extractDtoFromJsonObject(JSONObject jsonObject) { // TODO: 12/23/2019 check does stream exist
        val channelId = String.valueOf(jsonObject.getInt("channelId"));
        val isTestStream = jsonObject.getBoolean("isTestStream");
        val isOnline = jsonObject.getBoolean("online");
        val mixerId = jsonObject.getString("id");
        return new MixerStreamDto(channelId, isTestStream, isOnline, mixerId);
    }

    private boolean isNotSuccess(HttpResponse<JsonNode> response) {
        return !response.isSuccess();
    }
}
