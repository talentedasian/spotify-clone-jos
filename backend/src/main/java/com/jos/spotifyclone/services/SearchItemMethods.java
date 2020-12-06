package com.jos.spotifyclone.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified.Builder;
import com.wrapper.spotify.model_objects.specification.ExternalUrl;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;


@Component
public class SearchItemMethods {

	
	
	public SearchItemMethods() {
		// TODO Auto-generated constructor stub
	}
	
	com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = Caffeine.newBuilder().initialCapacity(150)
			.maximumSize(1500).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	
	ConcurrentMap<Object,Object> cacheAsMap = cache.asMap();
	
	org.apache.logging.log4j.Logger log = LogManager.getLogger(SearchItemMethods.class);
	
	
	
	
	public ArtistSimplified cacheAndPutArtists (String name, ExternalUrl url, String href) {
		
		
			if (!cacheAsMap.containsKey(name)) {
			ArtistSimplified artist = new ArtistSimplified.Builder().setName(name).setExternalUrls(url).setHref(href).build();
			
			cache.put(name, artist);
			log.info("Putting and getting from cache");
			System.out.println(artist);
			return artist;
			} else {
			return (ArtistSimplified) cache.getIfPresent(name);
			
			}
		
	}
	
	public AlbumSimplified cacheAndPutAlbums (String name, ExternalUrl url, String href, String imageUrl, 
			String artistName, ExternalUrl artistUrl, String artistHref) {
		if (!cacheAsMap.containsKey(name)) {
			AlbumSimplified album =  new AlbumSimplified.Builder().setName(name).setExternalUrls(url).setHref(href)
					.setArtists(cacheAndPutArtists(artistName, artistUrl, artistHref)).build();
						cache.put(name, album);
					return album;
		} else {
			return (AlbumSimplified) cache.getIfPresent(name);
		}
		
	}
	
	public TrackSimplified cacheAndPutTracks (String name, ExternalUrl url, String href, String artistName, ExternalUrl artistUrl, String artistHref) {
		if (!cacheAsMap.containsKey(name)) {
			TrackSimplified track = new TrackSimplified.Builder().setName(name).setExternalUrls(url).setHref(href)
					.setArtists(cacheAndPutArtists(artistName, artistUrl, artistHref)).build();
			return track;
		} else {
			return (TrackSimplified) cache.getIfPresent(name);
		}
		
	
	}
	 
	
	
	
}
