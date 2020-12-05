package com.jos.spotifyclone.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.jos.spotifyclone.model.album.AlbumName;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;

import jdk.jfr.ValueDescriptor;

@Component
@Configuration
public class SearchItem {

	private final SpotifyConnect spotifyConnect;
	
	private final SearchItemMethods itemMethods;


	com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = Caffeine.newBuilder().initialCapacity(150)
			.maximumSize(1500).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();

	@Autowired
	public SearchItem(SpotifyConnect spotifyConnect, SearchItemMethods itemMethods) {
		this.spotifyConnect = spotifyConnect;
		this.itemMethods = itemMethods;
	}

	public List<Object> searchAnItem(String item) throws ParseException, SpotifyWebApiException, IOException {

		SearchResult result = spotifyConnect.getSpotifyApi().searchItem(item, "artist,album,track").build().execute();

		ConcurrentMap<Object, Object> newCache = cache.asMap();
		List<Object> response = new ArrayList<>();
		for (Artist artists : result.getArtists().getItems()) {
			if (!newCache.containsKey(artists.getName())) {
				response.add(itemMethods.cacheAndPutArtists(artists.getName(), artists.getExternalUrls(), artists.getHref()));
			} else {
				response.add(cache.getIfPresent(artists.getName()));

			
		}

		for (AlbumSimplified albums : result.getAlbums().getItems()) {
			if (!newCache.containsKey(albums.getName())) {
				List<Object> albumToResponse = new ArrayList<>();
				albumToResponse.add(itemMethods.cacheAndPutAlbums(albums.getName(), albums.getExternalUrls(),
						albums.getHref(), albums.getImages()[0].getUrl()));
				for (ArtistSimplified artistsInAlbum : albums.getArtists()) {
					if (!newCache.containsKey(artists.getName())) {
					albumToResponse.add(itemMethods.cacheAndPutArtists(artists.getName(), artists.getExternalUrls(), artists.getHref()));
					} else {
						albumToResponse.add(cache.getIfPresent(artists.getName()));
				}
			}
			

		/*for (Track tracks : result.getTracks().getItems()) {
			List<Object> tracksToCache = new ArrayList<>();
			if (!newCache.containsKey(tracks.getName())) {
				
					tracksToCache.add(tracks.getName());
					tracksToCache.add(tracks.getExternalUrls());
					tracksToCache.add(tracks.getHref());
					for (ArtistSimplified artists : tracks.getArtists()) {
						if (!newCache.containsKey(artists.getName())) {
							List<Object> artistsToCacheInTracks = new ArrayList<>();
							artistsToCacheInTracks.add(artists.getName());
							artistsToCacheInTracks.add(artists.getExternalUrls());
							artistsToCacheInTracks.add(artists.getHref());
							tracksToCache.add(artistsToCacheInTracks);
							cache.put(artists.getName(), artistsToCacheInTracks);
						} else {
							tracksToCache.add(cache.getIfPresent(artists.getName()));
						}
							response.add(tracksToCache);
					}
					cache.put(tracks.getName(), tracksToCache);
				
					AlbumSimplified albums = tracks.getAlbum();
					if (!newCache.containsKey(tracks.getAlbum().getName())) {		
						tracksToCache.add(albums.getName());
						tracksToCache.add(albums.getExternalUrls());
						tracksToCache.add(albums.getHref());
						tracksToCache.add(albums.getImages()[0].getUrl());
						for (ArtistSimplified artists : albums.getArtists()) {
							if (!newCache.containsKey(artists.getName())) {
								List<Object> artistsToCacheInTracks = new ArrayList<>();
								artistsToCacheInTracks.add(artists.getName());
								artistsToCacheInTracks.add(artists.getExternalUrls());
								artistsToCacheInTracks.add(artists.getHref());
								tracksToCache.add(artistsToCacheInTracks);
								cache.put(artists.getName(), artistsToCacheInTracks);
								response.add(tracksToCache);
							} else {
								tracksToCache.add(cache.getIfPresent(artists.getName()));
							}
						}
					} else {
						tracksToCache.add(cache.getIfPresent(albums.getName()));
					}
				
				
			} else {
					response.add(tracks.getName());
				}		
		}*/

		return response;
	}

}
