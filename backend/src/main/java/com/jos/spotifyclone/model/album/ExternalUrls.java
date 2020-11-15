package com.jos.spotifyclone.model.album;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ExternalUrls {

	@JsonProperty("externalUrls")
	private ExternalUrls_ externalUrls;
}
