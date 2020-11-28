package com.jos.spotifyclone.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.catalina.connector.Response;
import org.apache.hc.core5.http.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.jos.spotifyclone.model.album.AlbumName;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
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
		
				
		
		
		
		
		AlbumSimplified[] albumsResult = result.getAlbums().getItems();
					
					Track[] tracksResult = result.getTracks().getItems();
					ConcurrentMap<Object,Object> newCache = cache.asMap();
					List<Object> response = new ArrayList<>();
					
					
					Arrays.stream(albumsResult).forEach(value -> {
						if (!newCache.containsKey(value.getName())) {
							cache.put(value.getName(), Arrays.stream(albumsResult).map(i -> new Album(i.getName(),
									Arrays.stream(i.getArtists()).map(ArtistSimplified::getName).collect(Collectors.toList()), 
									Arrays.stream(i.getArtists()).map(ArtistSimplified::getExternalUrls).collect(Collectors.toList()), 
									Arrays.stream(i.getImages()).map(Image::getUrl).collect(Collectors.toList()), i.getExternalUrls())).collect(Collectors.toList()));
							response.add(Arrays.stream(albumsResult).map(i -> new Album(i.getName(),
									Arrays.stream(i.getArtists()).map(ArtistSimplified::getName).collect(Collectors.toList()), 
									Arrays.stream(i.getArtists()).map(ArtistSimplified::getExternalUrls).collect(Collectors.toList()), 
									Arrays.stream(i.getImages()).map(Image::getUrl).collect(Collectors.toList()), i.getExternalUrls())).collect(Collectors.toList()));
							System.out.println("putting and not getting directly");
							} else {
								response.add(cache.getIfPresent(value.getName()));
								System.out.println("getting directly");
							}
					});


					System.out.println(cache.stats());
					
				
					
					
			
				
				return response;
			}
		
		
		
		
		


		
		
		

}
