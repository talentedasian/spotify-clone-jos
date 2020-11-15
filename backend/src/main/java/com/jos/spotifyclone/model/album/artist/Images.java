package com.jos.spotifyclone.model.album.artist;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class Images {

	
	
	@JsonProperty("url")
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Images() {
		// TODO Auto-generated constructor stub
	}

	public Images(String url) {
		super();
		this.url = url;
	}
	
	
}
