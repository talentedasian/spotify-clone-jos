package com.jos.spotifyclone.services;

import org.springframework.http.ResponseEntity;

public interface HttpHeadersResponse<T> {
	
	public ResponseEntity<T> responseEntity (T body, String appendingValue, org.springframework.http.HttpStatus status);
	
		
}
