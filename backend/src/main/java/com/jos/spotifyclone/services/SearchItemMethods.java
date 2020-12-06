package com.jos.spotifyclone.services;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.ExternalUrl;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;


@Component
public class SearchItemMethods {

	
	
	public SearchItemMethods() {
		// TODO Auto-generated constructor stub
	}
	
	com.github.benmanes.caffeine.cache.Cache<Object, ArtistSimplified> artistCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(30).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, AlbumSimplified> albumCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(30).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, TrackSimplified> trackCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(30).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	
	
	ConcurrentMap<Object,ArtistSimplified> artistCacheAsMap = artistCache.asMap();
	ConcurrentMap<Object,ArtistSimplified> albumCacheAsMap = artistCache.asMap();
	ConcurrentMap<Object,ArtistSimplified> trackCacheAsMap = artistCache.asMap();
	
	
	org.apache.logging.log4j.Logger log = LogManager.getLogger(SearchItemMethods.class);
	
	
	
	
	public ArtistSimplified cacheAndPutArtists (String name, ExternalUrl url, String href) {
			
			if (!artistCacheAsMap.containsKey(name)) {
				ArtistSimplified artist = new ArtistSimplified.Builder().setName(name).setExternalUrls(url).setHref(href).build();
				artistCache.put(name, artist);
				log.info("Putting and getting from Arist cache");
				return artist;
				} else {
					log.info("Getting Directly and not putting from Arist cache");
					return artistCache.getIfPresent(name);
				}
		
	}
	
	public AlbumSimplified cacheAndPutAlbums (String name, ExternalUrl url, String href, String imageUrl, 
			String artistName, ExternalUrl artistUrl, String artistHref) {
		if (!albumCacheAsMap.containsKey(name)) {
			AlbumSimplified album =  new AlbumSimplified.Builder().setName(name).setExternalUrls(url).setHref(href)
					.setArtists((ArtistSimplified)cacheAndPutArtists(artistName, artistUrl, artistHref)).build();
						albumCache.put(name, album);
						log.info("Putting and getting from Album cache");
					return album;
		} else {
			log.info("Getting Directly and not putting from Album Cache");
			return  albumCache.getIfPresent(name);
		}
		
	}
	
	public TrackSimplified cacheAndPutTracks (String name, ExternalUrl url, String href, String artistName, ExternalUrl artistUrl, String artistHref) {
		if (!trackCacheAsMap.containsKey(name)) {
			TrackSimplified track = new TrackSimplified.Builder().setName(name).setExternalUrls(url).setHref(href)
					.setArtists(cacheAndPutArtists(artistName, artistUrl, artistHref)).build();
			log.info("Putting and getting from Track cache");
			return track;
			
		} else {
			log.info("Getting Directly and not putting from Track Cache");
			return trackCache.getIfPresent(name);
			
		}
		
	
	}
	 
	
	
	
}
