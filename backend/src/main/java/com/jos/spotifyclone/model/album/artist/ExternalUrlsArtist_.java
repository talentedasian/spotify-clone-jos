package com.jos.spotifyclone.model.album.artist;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;


@Component
public class ExternalUrlsArtist_ {

	@JsonProperty("spotify")
	private String artistLink;

	public String getArtistLink() {
		return artistLink;
	}

	public void setArtistLink(String artistLink) {
		this.artistLink = artistLink;
	}

	public ExternalUrlsArtist_() {
		
	}

	public ExternalUrlsArtist_(String artistLink) {
		super();
		this.artistLink = artistLink;
	}

	@Override
	public String toString() {
		return "ExternalUrlsArtist_ [artistLink=" + artistLink + "]";
	}
	
	
}
