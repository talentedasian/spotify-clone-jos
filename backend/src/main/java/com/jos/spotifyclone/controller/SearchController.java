package com.jos.spotifyclone.controller;

import com.jos.spotifyclone.services.SearchItem;
import com.jos.spotifyclone.services.SpotifyConnect;
import com.neovisionaries.i18n.CountryCode;
import com.sun.net.httpserver.Headers;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
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
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
public class SearchController {

    private SpotifyConnect spotifyConnect;
    
    
    private SearchItem searchItem;

	
	
	
    @Autowired
    public SearchController(SpotifyConnect spotifyConnect, SearchItem searchItem) {
		super();
		// TODO Auto-generated constructor stub
		this.spotifyConnect = spotifyConnect;
		this.searchItem = searchItem;
		
	}
    
    

    //TODO ${ARTIST_NAME_HERE} needs value storing elsewhere where this controller can access the search term to return searched for artist data
    //http://localhost:8080/api/search/artist?id=drake
    @GetMapping("/artist")
    public Artist searchArtistController(@RequestParam String id) throws ParseException, IOException, SpotifyWebApiException {
        var response = spotifyConnect.getSpotifyApi().searchArtists(id).limit(1).build().execute();

        Builder artistResponse = new Artist.Builder();
        
        for(Artist artist : response.getItems()){
            artistResponse.setName(artist.getName())
            				.setFollowers(artist.getFollowers())
            				.setGenres(artist.getGenres())
            				.setExternalUrls(artist.getExternalUrls())
            				.setImages(artist.getImages())
            				.setId(artist.getId());
        
        }
        return artistResponse.build();
        
    }

    //http://localhost:8080/api/search/album?id=arianagrande
    @GetMapping("/album")
    public Map<String, Object> searchAlbumController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        var response = spotifyConnect.getSpotifyApi().searchAlbums(id).build().execute();

        List<AlbumModel> list = new ArrayList<>();
        for(AlbumSimplified album : response.getItems()){
            String name = album.getName();
            List<String> artist = new ArrayList<>();
            Image[] image = album.getImages();
            ExternalUrl externalUrl = album.getExternalUrls();

            ArtistSimplified[] artistArray  = album.getArtists();
            for(ArtistSimplified artistSimplified : artistArray){
                artist.add(artistSimplified.getName());
            }
            list.add(new AlbumModel(name, artist, image, externalUrl));
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("Album", list);
        return map;
    }

    //http://localhost:8080/api/search/episode?id=lauv
    @GetMapping("/episode")
    public Map<String, Object> searchEpisodeController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        var response = spotifyConnect.getSpotifyApi().searchEpisodes(id).build().execute();

        List<EpisodeModel> list = new ArrayList<>();
        for(EpisodeSimplified search : response.getItems()){
            String name = search.getName();
            String[] language = search.getLanguages();
            Image[] images = search.getImages();
            ExternalUrl externalUrls = search.getExternalUrls();
            String description = search.getDescription();

            list.add(new EpisodeModel(name, language, images, externalUrls, description));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("Episode", list);
        return map;
    }

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

    //http://localhost:8080/api/search/playlist?id=bieber
    @GetMapping("/playlist")
    public Map<String, Object> searchPlaylistController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        var response = spotifyConnect.getSpotifyApi().searchPlaylists(id).build().execute();

        List<PlaylistModel> list = new ArrayList<>();
        for(PlaylistSimplified playlist : response.getItems()){
            String href = playlist.getHref();
            ExternalUrl externalUrls = playlist.getExternalUrls();
            String playlistName = playlist.getName();
            PlaylistTracksInformation tracks = playlist.getTracks();
            Image[] playlistCover = playlist.getImages();

            list.add(new PlaylistModel(href, externalUrls, playlistName, tracks, playlistCover));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("Playlist", list);
        return map;
    }

    //http://localhost:8080/api/search/track?id=positions
    @GetMapping("/gg")
    public Map<String, Object> searchArtist(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        var response = spotifyConnect.getSpotifyApi().searchArtists(id).limit(1).build().execute();

        Map<String, Object> map = new HashMap<>();
        
        
        List<Object> list2 = new ArrayList<>();
        for(Artist artist : response.getItems()){
        	List<Object> list = new ArrayList<>();
        	Artist artistToResponse = new Artist.Builder().setExternalUrls(artist.getExternalUrls())
        			.setName(artist.getName())
        			.setImages(new Image.Builder().setUrl(artist.getImages()[0].getUrl()).build())
        			.setFollowers(artist.getFollowers())
        			.setPopularity(artist.getPopularity())
        			.build();
        	
        	list.add(artistToResponse);
        	
        	map.put("Artists", artistToResponse);
    		Track[] artistsTopTracks = spotifyConnect.getSpotifyApi().getArtistsTopTracks(artist.getId(), CountryCode.US).build().execute();
    			for (Track tracks: artistsTopTracks) {
    				
					Track trackToResponse = new Track.Builder().setExternalUrls(tracks.getExternalUrls())
							.setName(tracks.getName())
							.setPopularity(tracks.getPopularity())
							.setDurationMs(tracks.getDurationMs())
							.build();
							
					list2.add(trackToResponse);
					
					
																											  	
    				
    			}
            
            
        }

        map.put("Top Tracks", list2);
        
        return map;
    }
    
   
    @GetMapping("/item")
    public Map<String, List<Object>> searchItem(@RequestParam String item) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException, InterruptedException, ExecutionException {
    	
		
    
		return searchItem.searchAnItem(item);
    }
    

    
   
}
