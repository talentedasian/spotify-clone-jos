package com.jos.spotifyclone.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wrapper.spotify.model_objects.specification.ExternalUrl;

@Component
public class SearchItemMethods {

	
	
	public SearchItemMethods() {
		// TODO Auto-generated constructor stub
	}
	
	com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = Caffeine.newBuilder().initialCapacity(150)
			.maximumSize(1500).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	
	ConcurrentMap<Object,Object> cacheAsMap = cache.asMap();
	
	
	public List<Object> cacheAndPutArtists (String name, ExternalUrl url, String href) {
		List<Object> artistsToCache = new ArrayList<>();
		if (cacheAsMap.containsKey(name)) {
			artistsToCache.add(name);
			artistsToCache.add(url);
			artistsToCache.add(href);
			cache.put(name, artistsToCache);
		} else {
			artistsToCache.add(cache.getIfPresent(name));
		}
		return artistsToCache;
	}
	
	public List<Object> cacheAndPutAlbums (String name, ExternalUrl url, String href, String imageUrl) {
		List<Object> albumsToCache = new ArrayList<>();
		if (!cacheAsMap.containsKey(name)) {
			albumsToCache.add(name); 
			albumsToCache.add(url);
			albumsToCache.add(href);
			albumsToCache.add(imageUrl);
		} else {
			albumsToCache.add(cache.getIfPresent(name));
		}
		return albumsToCache;
	}
	
	public List<Object> cacheAndPutTracks (String name, ExternalUrl url, String href) {
		List<Object> tracksToCache = new ArrayList<>();
		if (!cacheAsMap.containsKey(name)) {
			tracksToCache.add(name);
			tracksToCache.add(url);
			tracksToCache.add(href);
		} else {
			tracksToCache.add(cache.getIfPresent(name));
		}
		return tracksToCache;
	
	}
	 
	
	
	
}
