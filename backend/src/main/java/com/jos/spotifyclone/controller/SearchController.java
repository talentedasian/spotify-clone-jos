package com.jos.spotifyclone.controller;
import com.jos.spotifyclone.services.SearchItem;
import com.jos.spotifyclone.services.SpotifyConnect;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.IModelObject;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.*;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
@RequestMapping("api/search")
@RestController
public class SearchController {

    private SpotifyConnect spotifyConnect;
    
    
    private SearchItem searchItem;
    
    private com.jos.spotifyclone.model.album.Track track;

	
	
	
    @Autowired
    public SearchController(SpotifyConnect spotifyConnect, SearchItem searchItem, com.jos.spotifyclone.model.album.Track track) {
		super();
		// TODO Auto-generated constructor stub
		this.spotifyConnect = spotifyConnect;
		this.searchItem = searchItem;
		this.track = track;
		
	}
    
    

    //TODO ${ARTIST_NAME_HERE} needs value storing elsewhere where this controller can access the search term to return searched for artist data
    //http://localhost:8080/api/search/artist?id=drake
    @GetMapping("/artist")
    public IModelObject searchArtistController(@RequestParam String id) throws ParseException, IOException, SpotifyWebApiException {
        return spotifyConnect.getSpotifyApi().searchArtists(id).build().execute();
    }

    //http://localhost:8080/api/search/album?id=arianagrande
    @GetMapping("/album")
    public Paging<AlbumSimplified> searchAlbumController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        return spotifyConnect.getSpotifyApi().searchAlbums(id).build().execute();
    }

    //http://localhost:8080/api/search/episode?id=lauv
    @GetMapping("/episode")
    public Paging<EpisodeSimplified> searchEpisodeController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        return spotifyConnect.getSpotifyApi().searchEpisodes(id).build().execute();
    }

    //http://localhost:8080/api/search/show?id=bieber
    @GetMapping("/show")
    public Paging<ShowSimplified> searchShowController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        return spotifyConnect.getSpotifyApi().searchShows(id).build().execute();
    }

    //http://localhost:8080/api/search/playlist?id=bieber
    @GetMapping("/playlist")
    public Paging<PlaylistSimplified> searchPlaylistController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        return spotifyConnect.getSpotifyApi().searchPlaylists(id).build().execute();
    }

    //http://localhost:8080/api/search/track?id=positions
    @GetMapping("/track")
    public Paging<Track> searchTrackController(@RequestParam String id) throws ParseException, SpotifyWebApiException, IOException {
        return spotifyConnect.getSpotifyApi().searchTracks(id).build().execute();
    }
    
    @GetMapping("/item")
    public List<Object> searchItem(@RequestParam String item) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException {
    	
    			
		
    
		return searchItem.searchAnItem(item);
    }
    

    @GetMapping("/currentPlayback")
    public List<Object> currentPlayback () throws ParseException, SpotifyWebApiException, IOException {
    		var response = spotifyConnect.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build().execute();
    		
    		List<Object> d = new ArrayList<>();
    		IPlaylistItem idk = response.getItem();
    		String j = idk.getName();
    		ExternalUrl url = idk.getExternalUrls();
    		d.add(j);
    		d.add(url);
    		d.add(idk.getUri());
    		return d;
    }
   
}
