package com.jos.spotifyclone.model.album.artist;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Component
public class Artist {

	private String name;
	 
	private String spotify;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpotify() {
		return spotify;
	}

	public void setSpotify(String spotify) {
		this.spotify = spotify;
	}

	public Artist() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Artist(String name, String spotify) {
		super();
		this.name = name;
		this.spotify = spotify;
	}

	@Override
	public String toString() {
		return "Artist [name=" + name + ", spotify=" + spotify + "]";
	}
	
	
	
	
}
