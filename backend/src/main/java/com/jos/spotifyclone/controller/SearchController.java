package com.jos.spotifyclone.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jos.spotifyclone.model.album.Albums;
import com.jos.spotifyclone.model.album.Items;
import com.jos.spotifyclone.model.album.artist.Artist;
import com.jos.spotifyclone.services.SpotifyConnect;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.IModelObject;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.*;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@RequestMapping("api/search")
@RestController
public class SearchController {
    @Autowired
    SpotifyConnect spotifyConnect;
    
    @Autowired
    Items items;
    
    @Autowired
    Albums albums;
    
    @Autowired
    Artist artist;
    
    

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
    public String searchItem(@RequestParam String item) throws ParseException, SpotifyWebApiException, IOException {
    	SearchResult result = spotifyConnect.getSpotifyApi().searchItem(item, "track,album,playlist").limit(4).build().execute();
    	Gson gson = new GsonBuilder().setLenient().create();
    	JsonObject album = com.google.gson.JsonParser.parseString(gson.toJson(result)).getAsJsonObject().getAsJsonObject("albums"); 
    	List<JsonArray> c  = new ArrayList<>();
    	c.add(album.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonArray("artists"));
    	c.add(album.getAsJsonArray("items").get(1).getAsJsonObject().getAsJsonArray("artists"));
    	c.add(album.getAsJsonArray("items").get(2).getAsJsonObject().getAsJsonArray("artists"));
    	c.add(album.getAsJsonArray("items").get(3).getAsJsonObject().getAsJsonArray("artists"));
    	JsonObject albumsd = null;
    	for (int i = 0; i < 3; i++) {
			albumsd = album.getAsJsonArray("items").get(i).getAsJsonObject();
		
    	
    			while (albumsd.has("availableMarkets") && albumsd.has("albumType") && albumsd.has("href") 
    					&& albumsd.has("id") && albumsd.has("releaseDate") && albumsd.has("releaseDatePrecision")&& albumsd.has("type") && albumsd.has("uri") 
    					&& albumsd.has("type") && albumsd.has("uri")) {
    					albumsd.remove("availableMarkets");
						albumsd.remove("href");
						albumsd.remove("id");
						albumsd.remove("releaseDate");
						albumsd.remove("releaseDatePrecision");
						albumsd.remove("type");
						albumsd.remove("uri");
    				}
    		
    	}
    	//System.out.println(lala);
    	
    	
    	return albumsd.toString();
    	
    	
    	
    	
    }

}
