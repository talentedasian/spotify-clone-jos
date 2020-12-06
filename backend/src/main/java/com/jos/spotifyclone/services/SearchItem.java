package com.jos.spotifyclone.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
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

	public ResponseEntity<Map<String, List<Object>>> searchAnItem(String item) throws ParseException, SpotifyWebApiException, IOException {

		SearchResult result = spotifyConnect.getSpotifyApi().searchItem(item, "artist,album,track").limit(10).build().execute();
		Map<String, Object> tracksToResponse = new HashMap<>();
 		
		List<Object> artistsToCache = new ArrayList<>();
		List<Object> albumsToCache = new ArrayList<>();
		List<Object> tracksToCache = new ArrayList<>();
		
		
		  for (Artist artists : result.getArtists().getItems()) {
	  		  artistsToCache.add(itemMethods.cacheAndPutArtists(artists.getName(),
  				  artists.getExternalUrls(), artists.getHref()));
	  		  	  response.put("Artists ", artistsToCache); 
			  
		  }
		  
		  for (AlbumSimplified albums : result.getAlbums().getItems()) { 
			 
				  for (ArtistSimplified artistsInAlbum : albums.getArtists()) {
	  				 albumsToCache.add(itemMethods.cacheAndPutAlbums(albums.getName(), albums.getExternalUrls(),
  						 albums.getHref(), albums.getImages()[0].getUrl(), artistsInAlbum.getName(), 
  						 artistsInAlbum.getExternalUrls(), artistsInAlbum.getHref()));
		  			
					  response.put("Albums", albumsToCache);		  	
				  }		  			
			  
		  }
			  
		for (Track tracks : result.getTracks().getItems()) {
			if (!tracks.getName().equalsIgnoreCase("gg")) {
	
				for (ArtistSimplified artistsInTracks : tracks.getArtists()) {
					tracksToCache.add(itemMethods.cacheAndPutTracks(tracks.getName(), tracks.getExternalUrls(),
						tracks.getHref(), artistsInTracks.getName(), artistsInTracks.getExternalUrls(), artistsInTracks.getHref()));
						tracksToResponse.put("Track Details", tracksToCache);
				}
			response.put("Tracks", tracksToCache);
			}
		}
		artistsToCache.add(spotifyConnect.getSpotifyApi().getAccessToken());
		
			
		return new ResponseEntity<Map<String,List<Object>>>(response, HttpStatus.OK);
		  	
	}
		  
}
