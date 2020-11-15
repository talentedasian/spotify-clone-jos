package com.jos.spotifyclone.model.album.artist;


import org.springframework.stereotype.Component;


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
