package com.jos.spotifyclone.model.album;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.JsonArray;
import com.jos.spotifyclone.model.album.artist.Artist;
import com.jos.spotifyclone.model.album.artist.Images;

@Component
@JsonFormat(shape = Shape.ARRAY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"artists", "spotify", "images", "name"})
public class Items {
	
	private List<JsonArray> artists;
	
	private ExternalUrls externalUrls;
	
	private List<Images> images;
	
	private String name;

	public List<JsonArray> getArtists() {
		return artists;
	}

	public void setArtists(List<JsonArray> artists) {
		this.artists = artists;
	}

	public ExternalUrls getExternalUrls() {
		return externalUrls;
	}

	public void setExternalUrls(ExternalUrls externalUrls) {
		this.externalUrls = externalUrls;
	}

	public List<Images> getImages() {
		return images;
	}

	public void setImages(List<Images> images) {
		this.images = images;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Items() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Items(List<JsonArray> artists, ExternalUrls externalUrls, List<Images> images, String name) {
		super();
		this.artists = artists;
		this.externalUrls = externalUrls;
		this.images = images;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Item [artists=" + artists + ", externalUrls=" + externalUrls + ", images=" + images + ", name=" + name
				+ "]";
	}
	
	
}
