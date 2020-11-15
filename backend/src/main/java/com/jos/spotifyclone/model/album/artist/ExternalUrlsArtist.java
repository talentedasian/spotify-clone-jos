package com.jos.spotifyclone.model.album.artist;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ExternalUrlsArtist {

	
	
	@JsonProperty("externalUrls")
	private ExternalUrlsArtist_ externalUrls;
	
	public ExternalUrlsArtist_ getExternalUrls() {
		return externalUrls;
	}

	public void setExternalUrls(ExternalUrlsArtist_ externalUrls) {
		this.externalUrls = externalUrls;
	}

	public ExternalUrlsArtist() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExternalUrlsArtist(ExternalUrlsArtist_ externalUrls) {
		super();
		this.externalUrls = externalUrls;
	}

	@Override
	public String toString() {
		return "ExternalUrlsArtist [externalUrls=" + externalUrls + "]";
	}
	
	
}
