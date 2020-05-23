package space.delusive.discord.broadcastbot.integration.impl;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.util.StringUtils;
import space.delusive.discord.broadcastbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.broadcastbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.broadcastbot.integration.TwitchIntegration;
import space.delusive.discord.broadcastbot.integration.dto.TwitchStreamDto;

import java.time.LocalDateTime;

@Log4j2
@RequiredArgsConstructor
public class TwitchIntegrationImpl implements TwitchIntegration {
    private final String getCurrentStreamUrl;
    private final String getOAuthTokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final Gson gson;

    private OAuthToken oauthToken;

    @Override
    public TwitchStreamDto getCurrentStream(String userName)
            throws NoCurrentStreamFoundException, UnsuccessfulRequestException {
        updateTokenIfNecessary();
        val authorizationHeader = StringUtils.capitalize(oauthToken.getTokenType()) + " " + oauthToken.getAccessToken();
        HttpResponse<JsonNode> response = Unirest.get(getCurrentStreamUrl)
                .routeParam("userName", userName)
                .header("Client-ID", clientId)
                .header("Authorization", authorizationHeader)
                .asJson();
        log.debug("Request to get current stream on Twitch channel with name \"{}\" has been sent. " +
                        "Following answer has been received. Status: \"{}\", Body: \"{}\"",
                userName, response.getStatusText(), response.getBody().toString());
        checkForSuccess(response);
        JSONArray data = response.getBody().getObject().getJSONArray("data");
        if (data.isEmpty()) {
            throw new NoCurrentStreamFoundException();
        }
        return gson.fromJson(data.getJSONObject(0).toString(), TwitchStreamDto.class);
    }

    /**
     * Twitch invalidates an oauth token after some time so we will need to get another one if invalidating happens
     */
    private void updateTokenIfNecessary() {
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
        log.debug("Request to get new OAuth token on Twitch has been sent. Following answer has been received. " +
                "Status: \"{}\", Body: \"{}\"", response.getStatusText(), response.getBody().toPrettyString());
        checkForSuccess(response);
        JSONObject jsonObject = response.getBody().getObject();
        // we are not able to use GSON here because we have to calculate expiresIn field value
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
