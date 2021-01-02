package com.jos.spotifyclone.services;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.ExternalUrl;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;


@Component
public class SearchItemMethods {
	
	com.github.benmanes.caffeine.cache.Cache<Object, ArtistSimplified> artistSimplifiedCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, Artist> artistCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, AlbumSimplified> albumSimplifiedCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, Track> trackCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	
	org.apache.logging.log4j.Logger log = LogManager.getLogger(SearchItemMethods.class);
	
	
	
	
	public Artist cacheAndPutArtists (String name, ExternalUrl url, String imageUrl) {
			if (!artistCache.asMap().containsKey(name)) {
				Artist artist = new Artist.Builder()
						.setName(name)
						.setExternalUrls(url)
						.setImages(new Image.Builder().setUrl(imageUrl).build())
						.setType(ModelObjectType.ARTIST)
						.build();
				artistCache.put(name, artist);
				log.info("Putting and getting from Artist cache");
				return artist;
				} else {
					log.info("Getting Directly and not putting from Artist cache");
					return artistCache.getIfPresent(name);
				}
		
	}
	
	public ArtistSimplified cacheAndPutArtistsSimplified (String name, ExternalUrl url) {
		if (!artistSimplifiedCache.asMap().containsKey(name)) {
			ArtistSimplified artist = new ArtistSimplified.Builder()
					.setName(name)
					.setExternalUrls(url)
					.build();
			artistSimplifiedCache.put(name, artist);
			log.info("Putting and getting from ArtistSimplified cache");
			return artist;
			} else {
				log.info("Getting Directly and not putting from ArtistSimplified cache");
				return artistSimplifiedCache.getIfPresent(name);
			}
	
	}
	
	public AlbumSimplified cacheAndPutAlbumsSimplified (String name, ExternalUrl url, String imageUrl, 
			String artistName, ExternalUrl artistUrl) {
		if (!albumSimplifiedCache.asMap().containsKey(name)) {
			AlbumSimplified album =  new AlbumSimplified.Builder()
					.setName(name)
					.setExternalUrls(url)
					.setImages(new Image.Builder().setUrl(imageUrl).build())
					.setArtists(cacheAndPutArtistsSimplified(artistName, artistUrl))
					.build();
						albumSimplifiedCache.put(name, album);
						log.info("Putting and getting from AlbumSimplified cache");
					return album;
		} else {
			log.info("Getting Directly and not putting from AlbumSimplified Cache");
			return albumSimplifiedCache.getIfPresent(name);
		}
		
	}
	
	public Track cacheAndPutTracks (String name, ExternalUrl url, String artistName, ExternalUrl artistUrl) {
		if (!trackCache.asMap().containsKey(name)) {
			Track track = new Track.Builder()
					.setName(name)
					.setExternalUrls(url)
					.setArtists(cacheAndPutArtistsSimplified(artistName, artistUrl))				
					.build();
			trackCache.put(name, track);
			log.info("Putting and getting from Track cache");
			return track;
			
		} else {
			log.info("Getting Directly and not putting from Track Cache");
			return trackCache.getIfPresent(name);
		}
		
	}
	
}
