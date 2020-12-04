package com.jos.spotifyclone.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.jos.spotifyclone.model.album.AlbumName;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;

import jdk.jfr.ValueDescriptor;


@Component
public class SearchItem {
	
	private final SpotifyConnect spotifyConnect;
	
	private final AlbumName albums;
	
	private final Album album;
	
	com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = Caffeine.newBuilder().initialCapacity(150)
			.maximumSize(1500).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	
	
	@Autowired
	public SearchItem (SpotifyConnect spotifyConnect, 
			AlbumName albums, Album album) {
		this.spotifyConnect = spotifyConnect;
		this.albums = albums;
		this.album = album;
	}
	
	public List<Object> searchAnItem(String item) throws ParseException, SpotifyWebApiException, IOException{
					
	SearchResult result = spotifyConnect.getSpotifyApi().searchItem(item, "track,album,playlist").build().execute();
					
		
		ConcurrentMap<Object,Object> newCache = cache.asMap();
		List<Object> response = new ArrayList<>();
				for (Artist  artists : result.getArtists().getItems()) {
					if (!newCache.containsKey(artists.getName())) {
						List<Object> artistsToCache = new ArrayList<>();
						artistsToCache.add(artists.getName());
						artistsToCache.add(artists.getExternalUrls());
						artistsToCache.add(artists.getHref());
						artistsToCache.add(artists.getImages()[0].getUrl());
						response.add(artistsToCache);
						cache.put(artists.getName(), artistsToCache);
					} else {
						response.add(cache.getIfPresent(artists.getName()));
						
					}
						
						
				}
		
						
				return response;
			}
		
		
		
		
		


		
		
		

}
