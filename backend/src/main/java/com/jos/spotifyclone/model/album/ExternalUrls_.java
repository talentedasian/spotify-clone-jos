package com.jos.spotifyclone.model.album;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ExternalUrls_ {
	
	
	@JsonProperty("spotify")
	private String name;

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExternalUrls_() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExternalUrls_(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "ExternalUrls_ [name=" + name + "]";
	}
	


}
