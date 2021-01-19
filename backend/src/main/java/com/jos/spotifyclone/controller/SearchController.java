package com.jos.spotifyclone.controller;

import com.jos.spotifyclone.services.ComputeEtagValue;
import com.jos.spotifyclone.services.HttpHeadersResponse;
import com.jos.spotifyclone.services.SearchItem;
import com.jos.spotifyclone.services.SpotifyConnect;
import com.neovisionaries.i18n.CountryCode;
import com.sun.net.httpserver.Headers;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.BadRequestException;
import com.wrapper.spotify.model_objects.IPlaylistItem;

import com.jos.spotifyclone.model.*;
import com.wrapper.spotify.model_objects.miscellaneous.PlaylistTracksInformation;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.model_objects.specification.Artist.Builder;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RequestMapping("api/search")
@RestController
public class SearchController implements HttpHeadersResponse<Map<String,List<Object>>>{

    private SpotifyConnect spotifyConnect;
    private SearchItem searchItem;
    private WebRequest request;

	
	
	
    @Autowired
    public SearchController(SpotifyConnect spotifyConnect, SearchItem searchItem, WebRequest request) {
		super();
		// TODO Auto-generated constructor stub
		this.spotifyConnect = spotifyConnect;
		this.searchItem = searchItem;
		this.request = request;
	}
    
    

    //TODO ${ARTIST_NAME_HERE} needs value storing elsewhere where this controller can access the search term to return searched for artist data
    //http://localhost:8080/api/search/artist?id=drake
    

    //START OF ALBUM ENDPOINT
    //http://localhost:8080/api/search/album?id=arianagrande
    @GetMapping("/album")
    public Album searchAlbum (@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException { 
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Album response = spotifyConnect.getSpotifyApi().getAlbum(id).build().execute();
    	Map<String,List<Object>> map = new HashMap<>();
    	List<Object> albumToResponse = new ArrayList<>();
    	
    	
		for (TrackSimplified tracks : response.getTracks().getItems()) {
    		for (ArtistSimplified artists : tracks.getArtists()) {
				Album albumBuilder = new Album.Builder()
	    				.setName(response.getName())
	    				.setId(response.getId())
	    				.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder().setUrl(response.getImages()[0].getUrl()).build())
	    				
	    				.build();
		    		
	    		albumToResponse.add(albumBuilder);
	    		map.put("Album", albumToResponse);
    		}
    	}
    	return response;
    }
    
//    @GetMapping("//")
//    public ResponseEntity<Map<String,List<Object>>> search (@RequestParam String id) {
//    		spotifyConnect.getSpotifyApi().getalbum
//    }
    
    
    
    
    
    
    
//    //http://localhost:8080/api/search/episode?id=lauv
//    @GetMapping("/episode")
//    public Map<String, Object> searchEpisodeController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
//        var response = spotifyConnect.getSpotifyApi().searchEpisodes(id).build().execute();
//
//        List<EpisodeModel> list = new ArrayList<>();
//        for(EpisodeSimplified search : response.getItems()){
//            String name = search.getName();
//            String[] language = search.getLanguages();
//            Image[] images = search.getImages();
//            ExternalUrl externalUrls = search.getExternalUrls();
//            String description = search.getDescription();
//
//            list.add(new EpisodeModel(name, language, images, externalUrls, description));
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("Episode", list);
//        return map;
//    }

    //http://localhost:8080/api/search/show?id=bieber
    @GetMapping("/show")
    public Map<String, Object> searchShowController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        var response = spotifyConnect.getSpotifyApi().searchShows(id).build().execute();

        List<ShowModel> list = new ArrayList<>();
        for(ShowSimplified show : response.getItems()){
            String description = show.getDescription();
            String name = show.getName();
            ExternalUrl externalUrls = show.getExternalUrls();

            list.add(new ShowModel(description, name, externalUrls));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("Show", list);
        return map;
    }

    //START OF PLAYLIST ENDPOINT
    //http://localhost:8080/api/search/playlist?id=bieber
    @GetMapping("/playlist")
    public ResponseEntity<Map<String,List<Object>>> searchPlaylistController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
        	return null;
        } 
        
    	Paging<PlaylistSimplified> response = spotifyConnect.getSpotifyApi().searchPlaylists(id).build().execute();
        Map<String,List<Object>> map = new HashMap<>();
        List<Object> playlistToResponse = new ArrayList<>();
        for (PlaylistSimplified playlist : response.getItems()) {
        	PlaylistSimplified playlistBuilder = new PlaylistSimplified.Builder()
        			.setName(playlist.getName())
        			.setId(playlist.getId())
        			.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder()
        					.setUrl(playlist.getImages()[0].getUrl())
        					.build())
    				.build();
			playlistToResponse.add(playlistBuilder);
			map.put("Playlist", playlistToResponse);
        }
        return responseEntity(map, id, HttpStatus.OK);
    }

    //START OF ARTIST ENDPOINT
    @GetMapping("/artist")
    public ResponseEntity<Map<String,List<Object>>> searchArtist(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Paging<Artist> response = spotifyConnect.getSpotifyApi().searchArtists(id).limit(1).build().execute();
        Map<String, List<Object>> map = new HashMap<>();
        List<Object> artistToResponse = new ArrayList<>();
        for(Artist artist : response.getItems()){
    		Artist artistBuilder = new Artist.Builder()
    				.setName(artist.getName())
    				.setId(artist.getId())
    				.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder()
    						.setUrl(artist.getImages()[0].getUrl())
    						.build())
    				.setFollowers(artist.getFollowers())
    				.setPopularity(artist.getPopularity())
    				.build();
        	
        	artistToResponse.add(artistBuilder);
        	map.put("Artist", artistToResponse);
        }	
        return responseEntity(map, id, HttpStatus.OK);
    }
    
    @GetMapping("/relatedArtist")
    public ResponseEntity<Map<String,List<Object>>> searchRelatedArtist(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException { 
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	} 
    	
    	Artist[] response = spotifyConnect.getSpotifyApi().getArtistsRelatedArtists(id).build().execute();
    	Map<String,List<Object>> map = new HashMap<>();
		List<Object> relatedArtistToResponse = new ArrayList<>();
    	for (Artist relatedArtist : response) {
    		Artist relatedArtistBuilder = new Artist.Builder()
    				.setName(relatedArtist.getName())
    				.setId(relatedArtist.getId())
    				.setImages(new com.wrapper.spotify.model_objects.specification.Image.Builder()
    						.setUrl(relatedArtist.getImages()[0].getUrl())
    						.build())
    				.build();
    		
    		relatedArtistToResponse.add(relatedArtistBuilder);
    		map.put("RelatedArtists", relatedArtistToResponse);
    	}
    	return responseEntity(map, id, HttpStatus.OK);
    }
    
    @GetMapping("/artistTopTrack")
    public ResponseEntity<Map<String,List<Object>>> searchArtistTopTrack(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException {
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Track[] response = spotifyConnect.getSpotifyApi().getArtistsTopTracks(id, CountryCode.US).build().execute();
    	Map<String,List<Object>> map = new HashMap<>();
    	List<Object> artistTopTrackToResponse = new ArrayList<>();
    	for (Track artistTopTrack : response) {
    		for (ArtistSimplified artistTopTrackArtist : artistTopTrack.getArtists()) {
	    		Track artistTopTrackBuilder = new Track.Builder()
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
	    		map.put("ArtistTopTrack", artistTopTrackToResponse);
    		}
    	}
    	return responseEntity(map, id, HttpStatus.OK);
    }
    
    @GetMapping("/artistAlbum")
    public Paging<AlbumSimplified> searchArtistAlbum(@RequestParam String id, @RequestParam int limit) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException {
    	if (request.checkNotModified(ComputeEtagValue.computeEtag(id))) {
    		return null;
    	}
    	
    	Paging<AlbumSimplified> response = spotifyConnect.getSpotifyApi().getArtistsAlbums(id).limit(limit).build().execute();
    	Map<String,List<Object>> map = new HashMap<>();
    	List<Object> artistAlbumToResponse = new ArrayList<>();
    	for (AlbumSimplified artistAlbum : response.getItems()) {
    		for (ArtistSimplified artistAlbumArtist : artistAlbum.getArtists()) {
	    		AlbumSimplified artistAlbumBuilder = new AlbumSimplified.Builder()
	    				.setName(artistAlbum.getName())
	    				.setId(artistAlbum.getId())
	    				.setArtists(new ArtistSimplified.Builder()
	    						.setName(artistAlbumArtist.getName())
	    						.setId(artistAlbumArtist.getId())
	    						.build())
	    				.build();
	    		
	    		artistAlbumToResponse.add(artistAlbumBuilder);
	    		map.put("ArtistAlbum", artistAlbumToResponse);
    		}
    	}
    	return response;
    }//END OF ARTIST ENDPOINT
   
    
    @GetMapping("/item")
    public ResponseEntity<Map<String,List<Object>>> searchItem(@RequestParam String item) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException {
    	
		return searchItem.searchAnItem(item);
    }



	@Override
	public ResponseEntity<Map<String, List<Object>>> responseEntity(Map<String, List<Object>> body,
			String appendingValue, HttpStatus status) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"" + ComputeEtagValue.computeEtag(appendingValue) + "\"");
		headers.setCacheControl("must-revalidate, max-age=345600, private");
		headers.setConnection("Keep-Alive");
		headers.set("Keep-Alive", "timeout=85");
		return new ResponseEntity<Map<String, List<Object>>>(body,headers, status);
	}
    

    
   
}
