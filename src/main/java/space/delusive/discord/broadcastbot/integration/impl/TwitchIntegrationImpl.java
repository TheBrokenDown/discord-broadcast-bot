package space.delusive.discord.broadcastbot.integration.impl;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.springframework.util.StringUtils;
import space.delusive.discord.broadcastbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.broadcastbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.broadcastbot.integration.TwitchIntegration;
import space.delusive.discord.broadcastbot.integration.dto.TwitchStreamDto;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TwitchIntegrationImpl implements TwitchIntegration {
    private final String getCurrentStreamUrl;
    private final String getOAuthTokenUrl;
    private final String clientId;
    private final String clientSecret;

    private OAuthToken oauthToken;

    @Override
    public TwitchStreamDto getCurrentStream(String userName) throws NoCurrentStreamFoundException, UnsuccessfulRequestException {
        updateTokenIfNecessary();
        HttpResponse<JsonNode> response = Unirest.get(getCurrentStreamUrl)
                .routeParam("userName", userName)
                .header("Client-ID", clientId)
                .header("Authorization", StringUtils.capitalize(oauthToken.getTokenType()) + " " + oauthToken.getAccessToken())
                .asJson();
        checkForSuccess(response);
        JSONArray data = response.getBody().getObject().getJSONArray("data");
        if (data.isEmpty()) throw new NoCurrentStreamFoundException();
        val streamInfo = (JSONObject) data.get(0);
        val streamId = streamInfo.getString("id");
        val userId = streamInfo.getString("user_id");
        val trueUserName = streamInfo.getString("user_name");
        val title = streamInfo.getString("title");
        return new TwitchStreamDto(streamId, userId, trueUserName, title);
    }

    private void updateTokenIfNecessary() { // twitch invalidates an oauth token after some time so we will need to get another one if invalidating happens
        // if token is going to be expired soon
        if (oauthToken == null || oauthToken.getExpiresIn().minusSeconds(10).isBefore(LocalDateTime.now())) {
            oauthToken = getNewToken();
        }
    }

    private OAuthToken getNewToken() {
        HttpResponse<JsonNode> response = Unirest.post(getOAuthTokenUrl)
                .routeParam("clientId", clientId)
                .routeParam("clientSecret", clientSecret)
                .asJson();
        checkForSuccess(response);
        JSONObject jsonObject = response.getBody().getObject();
        val accessToken = jsonObject.getString("access_token");
        val tokenType = jsonObject.getString("token_type");
        val expiresIn = LocalDateTime.now().plusSeconds(jsonObject.getInt("expires_in"));
        return new OAuthToken(accessToken, tokenType, expiresIn);
    }

    private void checkForSuccess(HttpResponse<JsonNode> httpResponse) {
        if (!httpResponse.isSuccess()) {
            throw new UnsuccessfulRequestException(
                    String.format("Response status code is not success. The code is %s and the body is %s",
                            httpResponse.getStatus(), httpResponse.getBody().toPrettyString()));
        }
    }

    @Value
    private static class OAuthToken {
        String accessToken;
        String tokenType;
        LocalDateTime expiresIn;
    }
}
