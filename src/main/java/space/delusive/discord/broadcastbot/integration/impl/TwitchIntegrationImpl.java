package space.delusive.discord.broadcastbot.integration.impl;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.val;
import space.delusive.discord.broadcastbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.broadcastbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.broadcastbot.integration.TwitchIntegration;
import space.delusive.discord.broadcastbot.integration.dto.TwitchStreamDto;

@AllArgsConstructor
public class TwitchIntegrationImpl implements TwitchIntegration {
    private final String url;
    private final String clientId;
    private final String oauthToken;

    @Override
    public TwitchStreamDto getCurrentStream(String userName) throws NoCurrentStreamFoundException, UnsuccessfulRequestException {
        HttpResponse<JsonNode> response = Unirest.get(url)
                .routeParam("userName", userName)
                .header("Client-ID", clientId)
                .header("Authorization", oauthToken)
                .asJson();
        if (isNotSuccess(response)) {
            throw new UnsuccessfulRequestException(
                    String.format("Response status code is not success. The code is %s and the body is %s",
                            response.getStatus(), response.getBody().toPrettyString()));
        }
        JSONArray data = response.getBody().getObject().getJSONArray("data");
        if (data.isEmpty()) throw new NoCurrentStreamFoundException();
        val streamInfo = (JSONObject) data.get(0);
        val streamId = streamInfo.getString("id");
        val userId = streamInfo.getString("user_id");
        val trueUserName = streamInfo.getString("user_name");
        val title = streamInfo.getString("title");
        return new TwitchStreamDto(streamId, userId, trueUserName, title);
    }

    private boolean isNotSuccess(HttpResponse response) {
        return !response.isSuccess();
    }
}
