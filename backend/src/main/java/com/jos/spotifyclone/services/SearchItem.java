package com.jos.spotifyclone.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;


@Component
public class SearchItem {

	private final SpotifyConnect spotifyConnect;
	private final SearchItemMethods itemMethods;

	@Autowired
	public SearchItem(SpotifyConnect spotifyConnect, SearchItemMethods itemMethods) {
		this.spotifyConnect = spotifyConnect;
		this.itemMethods = itemMethods;
	}
	
	public ResponseEntity<Map<String, List<Object>>> searchAnItem(String item, int limit) throws ParseException, SpotifyWebApiException, IOException{
		
		SearchResult result = spotifyConnect.getSpotifyApi().searchItem(item, "artist,album,track,playlist").limit(limit).build().execute();
		Map<String, List<Object>> response = new HashMap<>();
		
		List<String> duplicate = new ArrayList<>();
		
		List<Object> albumToResponse = new ArrayList<>();
		
		List<Object> trackToResponse =  new ArrayList<>();
		
		List<Object> artistToResponse = new ArrayList<>();
		
		List<Object> playlistToResponse = new ArrayList<>();

			for (Artist artists : result.getArtists().getItems()) {
				if (!duplicate.contains(artists.getName())) {
						duplicate.add(artists.getName());
						
						artistToResponse.add(itemMethods.cacheAndPutArtists(artists.getName(),artists.getId(), 
								artists.getImages()));
					} 
			}
			
			for (Track tracks : result.getTracks().getItems()) {
					for (ArtistSimplified artistsInTracks : tracks.getArtists()) {
						if (!duplicate.contains(tracks.getId())) {
							duplicate.add(tracks.getId());
							 
							trackToResponse.add(itemMethods.cacheAndPutTracks(tracks.getName(), tracks.getId(),
							artistsInTracks.getName(), artistsInTracks.getId(), tracks.getAlbum().getImages()));
					}
				}
			}
			
			
			for (AlbumSimplified albums : result.getAlbums().getItems()) {
				for (ArtistSimplified artistsInAlbums : albums.getArtists()) {
					albumToResponse.add(itemMethods.cacheAndPutAlbumsSimplified(albums.getName(), albums.getId(), albums.getImages(),
							artistsInAlbums.getName(), artistsInAlbums.getId()));
				}
			}
			
			for (PlaylistSimplified playlists : result.getPlaylists().getItems()) {
				playlistToResponse.add(itemMethods.cacheAndPutPlaylistsSimplified(playlists.getName(), playlists.getId(), playlists.getOwner(), playlists.getImages()));
			}
			
			response.put("Artists" , artistToResponse);
			response.put("Tracks" , trackToResponse);
			response.put("Albums", albumToResponse);
			response.put("Playlists", playlistToResponse);
			
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(spotifyConnect.getSpotifyApi().getAccessToken());
			
		return new ResponseEntity<Map<String, List<Object>>>(response, headers, HttpStatus.OK);
	}
		  
}
