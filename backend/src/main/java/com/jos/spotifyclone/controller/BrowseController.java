package com.jos.spotifyclone.controller;

import com.jos.spotifyclone.services.HttpHeadersResponse;
import com.jos.spotifyclone.services.SpotifyConnect;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@RequestMapping("api/browse")
@RestController
public class BrowseController implements HttpHeadersResponse<Object> {

    
    private final SpotifyConnect spotifyConnect;
    private final WebRequest request;
    
    @Autowired
    public BrowseController(SpotifyConnect spotifyConnect, WebRequest request) {
		this.spotifyConnect = spotifyConnect;
		this.request = request;
	}

    //https://developer.spotify.com/console/get-available-genre-seeds/
    //http://localhost:8080/api/browse/recommended?seed=emo
    //TODO can't access the data
    @GetMapping("/recommended")
    public Recommendations getRecommended(@RequestParam String seed) throws ParseException, SpotifyWebApiException, IOException {
        return spotifyConnect.getSpotifyApi().getRecommendations().seed_genres(seed).build().execute();
    }

    //http://localhost:8080/api/browse/new-releases
    @GetMapping("/new-releases")
    @Cacheable
    public ResponseEntity<Object> newReleases() throws ParseException, SpotifyWebApiException, IOException {
    	Paging<AlbumSimplified> response = spotifyConnect.getSpotifyApi().getListOfNewReleases().build().execute();

        Map<String,List<Object>> map = new HashMap<>();
        List<Object> newReleaseseToResponse = new ArrayList<>();
        for(AlbumSimplified album : response.getItems()){
        	var albumBuilder = new AlbumSimplified.Builder()
        			.setName(album.getName())
        			.setId(album.getId())
        			.setImages(new Image.Builder().setUrl(album.getImages()[0].getUrl()).build())
        			.build();
        	
        	newReleaseseToResponse.add(albumBuilder);
        	map.put("New-Releases", newReleaseseToResponse);
        }
        return responseEntity(map, null, HttpStatus.OK);
    }

	@Override
	public ResponseEntity<Object> responseEntity(Object body,
			String appendingValue, HttpStatus status) {
		var headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noStore().sMaxAge(Duration.ZERO));
		headers.setConnection("Keep-Alive");
		
		return new ResponseEntity<Object>(body, headers, status);
	}

	
}
