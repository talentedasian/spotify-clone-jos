package com.jos.spotifyclone.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface HttpHeadersResponse<T> {
	
	public ResponseEntity<T> responseEntity (Map<String,List<Object>> body, String appendingValue, org.springframework.http.HttpStatus status);
		
}
