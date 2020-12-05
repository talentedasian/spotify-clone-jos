package com.jos.spotifyclone.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wrapper.spotify.model_objects.specification.ExternalUrl;

@Component
public class SearchItemMethods {

	public SearchItemMethods() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public List<Object> cacheAndPutArtists (String name, ExternalUrl url, String href) {
		List<Object> artistsToCache = new ArrayList<>();
		artistsToCache.add(name);
		artistsToCache.add(url);
		artistsToCache.add(href);
		cache.put(name, artistsToCache);
		return artistsToCache;
	}
	
	public List<Object> cacheAndPutAlbums (String nam, ExternalUrl url, String href, String imageUrl) {
		List<Object> albumsToCache = new ArrayList<>();
		
	}
	
	
	com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = Caffeine.newBuilder().initialCapacity(150)
			.maximumSize(1500).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
}
