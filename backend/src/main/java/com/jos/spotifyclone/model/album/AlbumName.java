package com.jos.spotifyclone.model.album;


import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;
import com.wrapper.spotify.model_objects.specification.ExternalUrl;


@Component
public class AlbumName{
	
	private String name;

	public AlbumName() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AlbumName(String name) {
		super();
		this.name = name;
	}
	
	
}