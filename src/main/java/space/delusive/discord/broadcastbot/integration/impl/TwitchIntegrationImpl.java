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
import space.delusive.discord.broadcastbot.exception.InvalidTwitchChannelNameException;
import space.delusive.discord.broadcastbot.exception.NoCurrentStreamFoundException;
import space.delusive.discord.broadcastbot.exception.UnsuccessfulRequestException;
import space.delusive.discord.broadcastbot.integration.TwitchIntegration;
import space.delusive.discord.broadcastbot.integration.dto.TwitchStreamDto;
import space.delusive.discord.broadcastbot.integration.dto.TwitchUserInfoDto;

import java.time.LocalDateTime;

@Log4j2
@RequiredArgsConstructor
public class TwitchIntegrationImpl implements TwitchIntegration {
    private static final int HTTP_BAD_REQUEST = 400;

    private final String getCurrentStreamUrl;
    private final String getUserInfoUrl;
    private final String getOAuthTokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final Gson gson;

    private OAuthToken oauthToken;

    @Override
    public TwitchStreamDto getCurrentStream(String userName)
            throws NoCurrentStreamFoundException, UnsuccessfulRequestException {
        updateTokenIfNecessary();
        HttpResponse<JsonNode> response = Unirest.get(getCurrentStreamUrl)
                .routeParam("userName", userName)
                .header("Client-ID", clientId)
                .header("Authorization", getAuthorizationHeader())
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

    @Override
    public TwitchUserInfoDto getUserInfo(String userName) throws InvalidTwitchChannelNameException {
        updateTokenIfNecessary();
        HttpResponse<JsonNode> response = Unirest.get(getUserInfoUrl)
                .routeParam("userName", userName)
                .header("Client-ID", clientId)
                .header("Authorization", getAuthorizationHeader())
                .asJson();
        log.debug("Request to get info about user \"{}\" from Twitch has been sent. " +
                        "Following answer has been received. Status: \"{}\", Body: \"{}\"",
                userName, response.getStatusText(), response.getBody().toString());
        checkGetUserInfoResponseForSuccess(response);
        return gson.fromJson(response.getBody().getObject().toString(), TwitchUserInfoDto.class);
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

    private String getAuthorizationHeader() {
        return StringUtils.capitalize(oauthToken.getTokenType()) + " " + oauthToken.getAccessToken();
    }

    private void checkGetUserInfoResponseForSuccess(HttpResponse<JsonNode> httpResponse) {
        if (httpResponse.getStatus() == HTTP_BAD_REQUEST) {
            throw new InvalidTwitchChannelNameException();
        }
        checkForSuccess(httpResponse);
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
