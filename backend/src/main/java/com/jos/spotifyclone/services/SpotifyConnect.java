package com.jos.spotifyclone.services;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URI;

@Component
public class SpotifyConnect {
    private final SpotifyApi spotifyApi;
    private final AuthorizationCodeUriRequest.Builder authorizationCodeUriRequestBuilder;


    public SpotifyConnect(
            @Value("${spotify.api.clientId}") String clientId,
            @Value("${spotify.api.secretId}") String secretId,
            @Value("${spotify.api.redirectUri}") String redirectUri
    ) {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(secretId)
                .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                .build();
       
        this.authorizationCodeUriRequestBuilder = spotifyApi.authorizationCodeUri().scope("user-read-recently-played " +
                "user-read-playback-position " +
                "user-top-read " +
                "playlist-modify-private " +
                "playlist-read-collaborative " +
                "playlist-read-private " +
                "playlist-modify-public " +
                "user-read-email " +
                "user-read-private " +
                "user-follow-read " +
                "user-follow-modify " +
                "user-library-modify " +
                "user-library-read " +
                "user-read-currently-playing " +
                "ugc-image-upload " +
                "user-read-playback-state " +
                "user-modify-playback-state");
    }


    public URI requestAccessToken () {
        final URI uri = authorizationCodeUriRequestBuilder.build().execute();
        return uri;
    }

    public void addAuthCode(String code) throws ParseException, SpotifyWebApiException, IOException {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

        // Set access and refresh token for further "spotifyApi" object usage
        spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }
}
