package com.jos.spotifyclone;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.wrapper.spotify.exceptions.detailed.BadRequestException;
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;

@ControllerAdvice
public class BadRequestExceptionHandling extends ResponseEntityExceptionHandler {

	
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<Object> badRequest (BadRequestException ex , WebRequest req) {
		Map<String,String> body = new HashMap<>();
		body.put("Status", "400");
		body.put("Reason", "Bad Query Paramaters");
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseBody
	public ResponseEntity<Object> unAuthorized (UnauthorizedException ex, WebRequest req) {
		Map<String,String> body = new HashMap<>();
		body.put("Status", "401");
		body.put("Reason", "Invalid Access Token");
		return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.UNAUTHORIZED, req);
	}
	
	
}
