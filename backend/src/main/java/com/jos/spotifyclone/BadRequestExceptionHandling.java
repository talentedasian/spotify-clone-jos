package com.jos.spotifyclone;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

import com.jos.spotifyclone.services.SpotifyConnect;
import com.wrapper.spotify.exceptions.detailed.BadRequestException;
import com.wrapper.spotify.exceptions.detailed.NotFoundException;
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;

@ControllerAdvice
public class BadRequestExceptionHandling extends ResponseEntityExceptionHandler {

	@Autowired
	private SpotifyConnect spotifyConnect;
	
	@ExceptionHandler(BadRequestException.class)
	@ResponseBody
	public ResponseEntity<Object> badRequest (BadRequestException ex , WebRequest req) {
		Map<String,String> body = new HashMap<>();
		body.put("Status", "400");
		body.put("Reason", "Bad Query Paramaters");
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	public RedirectView unAuthorized () {
		return new RedirectView(spotifyConnect.requestAccessToken().toString());
	}
	
	@ExceptionHandler(NotFoundException.class)
	@ResponseBody
	public ResponseEntity<Object> notFound (NotFoundException ex, WebRequest req) {
		Map<String,String> body = new HashMap<>();
		body.put("Status", "400");
		body.put("Reason", "Query Parameter Found No Resource");
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, req);
	}
	
	@ExceptionHandler(ArrayIndexOutOfBoundsException.class)
	@ResponseBody
	public ResponseEntity<Object> indexOutOfBounds (ArrayIndexOutOfBoundsException ex, WebRequest req) {
		Map<String,String> body = new HashMap<>();
		body.put("Status", "500");
		body.put("Reason", "Index Out Of Bounds");
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, req);	
	}

}
