package com.jos.spotifyclone.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;

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
	
	Map<String, List<Object>> response = new HashMap<>();

	public Map<String, List<Object>> searchAnItem(String item) throws ParseException, SpotifyWebApiException, IOException {

		SearchResult result = spotifyConnect.getSpotifyApi().searchItem(item, "artist,album,track").limit(10).build().execute();
		Map<String, Object> tracksToResponse = new HashMap<>();
 		
		List<Object> artistsToCache = new ArrayList<>();
		List<Object> tracksToCache = new ArrayList<>();
		List<Object> albumsToCache = new ArrayList<>();
		
		  for (Artist artists : result.getArtists().getItems()) {
		  if (!artistsToCache.contains(itemMethods.cacheAndPutArtists(artists.getName(),
		  artists.getExternalUrls(), artists.getHref()))) {
			  artistsToCache.add(itemMethods.cacheAndPutArtists(artists.getName(),
		  artists.getExternalUrls(), artists.getHref()));
			  response.put("Artists ", artistsToCache); 
			  }
		  }
		  
//		  for (AlbumSimplified albums : result.getAlbums().getItems()) { 
//			  if (!albumsToCache.contains(itemMethods.cacheAndPutAlbums(albums.getName(), albums.getExternalUrls(), albums.getHref(), albums.getImages()[0].getUrl()))) {
//				  
//				  albumsToCache.add(itemMethods.cacheAndPutAlbums(albums.getName(),albums.getExternalUrls(),albums.getHref(), albums.getImages()[0].getUrl()));
//				  for (ArtistSimplified artistsInAlbum : albums.getArtists()) {
//					  albumsToCache.add(itemMethods.cacheAndPutArtists(artistsInAlbum.getName(), artistsInAlbum.getExternalUrls(), artistsInAlbum.getHref())); 
//		  			
//					  response.put("Albums", albumsToCache);		  	
//				  }		  			
//			  }
//		  }
//			  
//		for (Track tracks : result.getTracks().getItems()) {
//			if (!tracks.getName().equalsIgnoreCase("gg")) {
//				
//				
//				for (ArtistSimplified artistsInTracks : tracks.getArtists()) {
//				tracksToCache.add(itemMethods.cacheAndPutTracks(tracks.getName(), tracks.getExternalUrls(),
//						tracks.getHref()));
//				tracksToResponse.put("Track Details", tracksToCache);
//				}
//			response.put("Tracks", tracksToResponse);
//			}
//		}
//		
			
		return response;
	
	}
		  
}
