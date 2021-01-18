package com.jos.spotifyclone.services;

import java.util.Base64;

public class ComputeEtagValue {
	
	public static String computeEtag (String appendingValue) {
		String etagNounce = "http-Caching-9283SEASNKMEU21A" + appendingValue + "E-tagDUIS*#732HA";
		String encodedString = Base64.getEncoder().encodeToString(etagNounce.getBytes());
		
		return encodedString;
	}

}
