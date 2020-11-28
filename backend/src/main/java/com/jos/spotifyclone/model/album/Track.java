package com.jos.spotifyclone.model.album;

import java.util.List;

import org.springframework.stereotype.Component;

import com.wrapper.spotify.model_objects.specification.ExternalUrl;

@Component
public class Track {
	
	
	private String name;
	
	private List<String> artists;

	private List<ExternalUrl> artistUrl;
	
	private ExternalUrl trackUrl;
	
	private List<String>imageUrl;

	public List<ExternalUrl> getArtistUrl() {
		return artistUrl;
	}

	public void setArtistUrl(List<ExternalUrl> artistUrl) {
		this.artistUrl = artistUrl;
	}

	public String getName() {
		return name;
	}

	public List<String> getArtists() {
		return artists;
	}

	public ExternalUrl getTrackUrl() {
		return trackUrl;
	}

	public List<String> getImageUrl() {
		return imageUrl;
	}

	public Track() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Track(String name, List<String> artists, List<ExternalUrl> artistUrl, ExternalUrl trackUrl,
			List<String> imageUrl) {
		super();
		this.name = name;
		this.artists = artists;
		this.artistUrl = artistUrl;
		this.trackUrl = trackUrl;
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "Track [name=" + name + ", artists=" + artists + ", artistUrl=" + artistUrl + ", trackUrl=" + trackUrl
				+ ", imageUrl=" + imageUrl + "]";
	}
	
	

	
	
}
