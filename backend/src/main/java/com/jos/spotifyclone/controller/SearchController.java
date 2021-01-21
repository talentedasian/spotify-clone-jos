package com.jos.spotifyclone.controller;

import com.jos.spotifyclone.services.ComputeEtagValue;
import com.jos.spotifyclone.services.HttpHeadersResponse;
import com.jos.spotifyclone.services.SearchItem;
import com.jos.spotifyclone.services.SpotifyConnect;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import com.wrapper.spotify.model_objects.specification.*;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RequestMapping("api/search")
@RestController
public class SearchController implements HttpHeadersResponse<Object>{

    private SpotifyConnect spotifyConnect;
    private SearchItem searchItem;
    private WebRequest request;
    
    @Autowired
    public SearchController(SpotifyConnect spotifyConnect, SearchItem searchItem, WebRequest request) {
		super();
		this.spotifyConnect = spotifyConnect;
		this.searchItem = searchItem;
		this.request = request;
	}
    
    //START OF ALBUM ENDPOINT
    //NOT YET DONE
    @GetMapping("/album")
    public ResponseEntity<Object> searchAlbum (@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException { 
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Album response = spotifyConnect.getSpotifyApi().getAlbum(id).build().execute();
    	Map<String,List<Object>> map = new HashMap<>();
    	List<Object> albumToResponse = new ArrayList<>();
    	
		for (TrackSimplified tracks : response.getTracks().getItems()) {
    		for (ArtistSimplified artists : tracks.getArtists()) {
				var albumBuilder = new Album.Builder()
	    				.setName(response.getName())
	    				.setId(response.getId())
	    				.setArtists(new ArtistSimplified.Builder()
	    						.setName(artists.getName())
	    						.setId(artists.getId())
	    						.build())
	    				.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder().setUrl(response.getImages()[0].getUrl()).build())
	    				.setReleaseDate(response.getReleaseDate())
	    				.build();
		    		
	    		albumToResponse.add(albumBuilder);
    		}
		}
		map.put("Album", albumToResponse);
    	return responseEntity(map, id, HttpStatus.OK);
    }

    @GetMapping("albumTrack")
    public ResponseEntity<Object> searchAlbumTrack (@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Paging<TrackSimplified> response = spotifyConnect.getSpotifyApi().getAlbumsTracks(id).limit(50).build().execute();
    	Map<String,List<Object>> map = new HashMap<>();
    	List<Object> albumTrackToResponse = new ArrayList<>();
    	
    	for (TrackSimplified albumTrack : response.getItems()) {
    		for (ArtistSimplified albumTrackArtist : albumTrack.getArtists()) {
	    		var albumTrackBuilder = new TrackSimplified.Builder()
	    				.setName(albumTrack.getName())
	    				.setId(albumTrack.getId())
	    				.setDurationMs(albumTrack.getDurationMs())
	    				.setTrackNumber(albumTrack.getTrackNumber())
	    				.setArtists(new ArtistSimplified.Builder()
	    						.setName(albumTrackArtist.getName())
	    						.setId(albumTrackArtist.getId())
	    						.build())
	    				.build();
	    		
	    		albumTrackToResponse.add(albumTrackBuilder);
    		}
    	}
    	map.put("AlbumTrack", albumTrackToResponse);
    	return responseEntity(map, id, HttpStatus.OK);
    }// END OF ALBUM ENDPOINT	
    

    //START OF PLAYLIST ENDPOINT
    //http://localhost:8080/api/search/playlist?id=bieber
    @GetMapping("/playlist")
    public ResponseEntity<Object> searchPlaylist(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
        	return null;
        } 
        
    	Paging<PlaylistSimplified> response = spotifyConnect.getSpotifyApi().searchPlaylists(id).build().execute();
        Map<String,List<Object>> map = new HashMap<>();
        List<Object> playlistToResponse = new ArrayList<>();
        for (PlaylistSimplified playlist : response.getItems()) {
        	var playlistBuilder = new PlaylistSimplified.Builder()
        			.setName(playlist.getName())
        			.setId(playlist.getId())
        			.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder()
        					.setUrl(playlist.getImages()[0].getUrl())
        					.build())
    				.build();
			playlistToResponse.add(playlistBuilder);
        }
        map.put("Playlist", playlistToResponse);
        return responseEntity(map, id, HttpStatus.OK);
    }
    
    @GetMapping("/playlistInfo")
    public ResponseEntity<Object> searchPlaylistTrack (@RequestParam String id, final Paging.Builder<PlaylistTrack> builder) throws ParseException, SpotifyWebApiException, IOException {
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Playlist response = spotifyConnect.getSpotifyApi().getPlaylist(id).build().execute();
    	List<Object> playlistInfoToResponse = new ArrayList<>();
    	for (PlaylistTrack playlistInfoTrack : response.getTracks().getItems() ) {
    		var playlistTrackBuilder= new PlaylistTrack.Builder()
    				.setTrack(playlistInfoTrack.getTrack())
    				.build();
    		PlaylistTrack[] playlistTrack = {playlistTrackBuilder};
    		
    		var playlistInfoBuilder = new Playlist.Builder()
    				.setName(response.getName())
    				.setId(response.getId())
    				.setImages(new Image.Builder().setUrl(response.getImages()[0].getUrl()).build())
    				.setOwner(response.getOwner())
    				.setFollowers(response.getFollowers())
    				.setTracks(new Paging.Builder<PlaylistTrack>().setItems(playlistTrack).build())
    				.build();
    		
    		playlistInfoToResponse.add(playlistInfoBuilder);
    	}
    	
    	return responseEntity(playlistInfoToResponse, id, HttpStatus.OK);
    }
    
    //START OF ARTIST ENDPOINT
    @GetMapping("/artist")
    public ResponseEntity<Object> searchArtist(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Artist response = spotifyConnect.getSpotifyApi().getArtist(id).build().execute();
        List<Object> artistToResponse = new ArrayList<>();
		var artistBuilder = new Artist.Builder()
				.setName(response.getName())
				.setId(response.getId())
				.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder()
						.setUrl(response.getImages()[0].getUrl())
						.build())
				.setFollowers(response.getFollowers())
				.setPopularity(response.getPopularity())
				.build();
    	
    	artistToResponse.add(artistBuilder);
        return responseEntity(artistToResponse, id, HttpStatus.OK);
    }
    
    @GetMapping("/relatedArtist")
    public ResponseEntity<Object> searchRelatedArtist(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException { 
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	} 
    	
    	Artist[] response = spotifyConnect.getSpotifyApi().getArtistsRelatedArtists(id).build().execute();
		List<Object> relatedArtistToResponse = new ArrayList<>();
    	for (Artist relatedArtist : response) {
    		var relatedArtistBuilder = new Artist.Builder()
    				.setName(relatedArtist.getName())
    				.setId(relatedArtist.getId())
    				.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder()
    						.setUrl(relatedArtist.getImages()[0].getUrl())
    						.build())
    				.build();
    		
    		relatedArtistToResponse.add(relatedArtistBuilder);
    	}
    	return responseEntity(relatedArtistToResponse, id, HttpStatus.OK);
    }
    
    @GetMapping("/artistTopTrack")
    public ResponseEntity<Object> searchArtistTopTrack(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException {
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Track[] response = spotifyConnect.getSpotifyApi().getArtistsTopTracks(id, CountryCode.US).build().execute();
    	List<Object> artistTopTrackToResponse = new ArrayList<>();
    	for (Track artistTopTrack : response) {
    		for (ArtistSimplified artistTopTrackArtist : artistTopTrack.getArtists()) {
	    		var artistTopTrackBuilder = new Track.Builder()
	    				.setName(artistTopTrack.getName())
	    				.setId(artistTopTrack.getId())
	    				.setAlbum(new AlbumSimplified.Builder()
	    						.setName(artistTopTrackArtist.getName())
	    						.setId(artistTopTrackArtist.getId())
	    						.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder()
	    								.setUrl(artistTopTrack.getAlbum().getImages()[0].getUrl())
	    								.build()).
	    						build())
						.build();
				
	    		artistTopTrackToResponse.add(artistTopTrackBuilder);
    		}
    	}
    	return responseEntity(artistTopTrackToResponse, id, HttpStatus.OK);
    }
    
    @GetMapping("/artistAlbum")
    public ResponseEntity<Object> searchArtistAlbum(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException {
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Paging<AlbumSimplified> response = spotifyConnect.getSpotifyApi().getArtistsAlbums(id).limit(10).build().execute();
    	List<Object> artistAlbumToResponse = new ArrayList<>();
    	for (AlbumSimplified artistAlbum : response.getItems()) {
    		for (ArtistSimplified artistAlbumArtist : artistAlbum.getArtists()) {
	    		var artistAlbumBuilder = new AlbumSimplified.Builder()
	    				.setName(artistAlbum.getName())
	    				.setId(artistAlbum.getId())
	    				.setArtists(new ArtistSimplified.Builder()
	    						.setName(artistAlbumArtist.getName())
	    						.setId(artistAlbumArtist.getId())
	    						.build())
	    				.build();
	    		
	    		artistAlbumToResponse.add(artistAlbumBuilder);
    		}
    	}
    	return responseEntity(artistAlbumToResponse, id, HttpStatus.OK);
    }//END OF ARTIST ENDPOINT
   
    
    @GetMapping("/item")
    public ResponseEntity<Map<String,List<Object>>> searchItem(@RequestParam String item) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException {
    	Map<String, List<Object>> map = searchItem.searchAnItem(item).getBody();
    	var headers = new HttpHeaders();
    	headers.setCacheControl(CacheControl.noStore());
    	
		return new ResponseEntity<Map<String,List<Object>>>(map, headers, HttpStatus.OK);
    }
	@Override
	public ResponseEntity<Object> responseEntity (Object body,
			String appendingValue, HttpStatus status) {
		var headers = new HttpHeaders();
		headers.setETag("\"" + ComputeEtagValue.computeEtag(appendingValue) + "\"");
		headers.setCacheControl("must-revalidate, max-age=345600, private");
		
		return new ResponseEntity<Object>(body,headers, status);
	}
    

    
   
}
