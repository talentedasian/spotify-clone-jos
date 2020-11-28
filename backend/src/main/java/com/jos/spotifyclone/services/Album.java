package com.jos.spotifyclone.services;

import java.util.List;

import org.springframework.stereotype.Component;

import com.wrapper.spotify.model_objects.specification.ExternalUrl;


@Component
public class Album {

	
	private String name;
	
	private List<String> artistName;
	
	private List<ExternalUrl> artistUrl;
	
	private List<String> imageUrl;
	
	private ExternalUrl url;
	
	public ExternalUrl getUrl() {
		return url;
	}
	public String getName() {
		return name;
	}
	public List<String> getArtistName() {
		return artistName;
	}
	public List<ExternalUrl> getArtistUrl() {
		return artistUrl;
	}
	public List<String> getImageUrl() {
		return imageUrl;
	}
	public Album() {
		// TODO Auto-generated constructor stub
	}
	public Album(String name, List<String> artistName, List<ExternalUrl> artistUrl, List<String> imageUrl, ExternalUrl url) {
		this.name = name;
		this.artistName = artistName;
		this.artistUrl = artistUrl;
		this.imageUrl = imageUrl;
		this.url = url;
	}
	
}
