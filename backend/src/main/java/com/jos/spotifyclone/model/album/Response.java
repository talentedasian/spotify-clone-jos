package com.jos.spotifyclone.model.album;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;

@Component
public class Response {
	
	private List<Albums>albums;

	public Paging<AlbumSimplified> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Albums> albums) {
		this.albums = albums;
	}

	public Response() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Response(List<Albums> albums) {
		super();
		this.albums = albums;
	}

	@Override
	public String toString() {
		return "Response [albums=" + albums + "]";
	}
	
	
		
}
