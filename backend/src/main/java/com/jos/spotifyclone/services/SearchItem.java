package com.jos.spotifyclone.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;

@Component
@Configuration
public class SearchItem {

	private final SpotifyConnect spotifyConnect;
	
	private final SearchItemMethods itemMethods;


	
	@Autowired
	public SearchItem(SpotifyConnect spotifyConnect, SearchItemMethods itemMethods) {
		this.spotifyConnect = spotifyConnect;
		this.itemMethods = itemMethods;
	}

	public List<Object> searchAnItem(String item) throws ParseException, SpotifyWebApiException, IOException {

		SearchResult result = spotifyConnect.getSpotifyApi().searchItem(item, "artist,album,track").build().execute();

		List<Object> response = new ArrayList<>();
		for (Artist artists : result.getArtists().getItems()) {
			response.add(itemMethods.cacheAndPutArtists(artists.getName(), artists.getExternalUrls(), artists.getHref()));
			}

		for (AlbumSimplified albums : result.getAlbums().getItems()) {
			List<Object> albumToResponse = new ArrayList<>();
				
			for (ArtistSimplified artistsInAlbum : albums.getArtists()) {
				albumToResponse.add(itemMethods.cacheAndPutAlbums(albums.getName(), albums.getExternalUrls(),albums.getHref(), albums.getImages()[0].getUrl()));
				if (!itemMethods.cacheAsMap.containsKey(albums.getName())) {
				albumToResponse.add(itemMethods.cacheAndPutArtists(artistsInAlbum.getName(), artistsInAlbum.getExternalUrls(), artistsInAlbum.getHref()));
				} else {
					albumToResponse.add(itemMethods.cache.getIfPresent(artistsInAlbum.getName()));
					}
				}
				
			}
		
			
		return response;
	
	}
}
