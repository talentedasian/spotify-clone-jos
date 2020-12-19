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

	
	
	public SearchItemMethods() {
		// TODO Auto-generated constructor stub
	}
	
	com.github.benmanes.caffeine.cache.Cache<Object, ArtistSimplified> artistSimplifiedCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(30).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, Artist> artistCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(30).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, AlbumSimplified> albumSimplifiedCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(30).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, Album> albumCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(30).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<Object, Track> trackCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(30).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	
	ConcurrentMap<Object, ArtistSimplified> artistSimplifiedCacheAsMap = artistSimplifiedCache.asMap();
	ConcurrentMap<Object,Artist> artistCacheAsMap = artistCache.asMap();
	ConcurrentMap<Object,AlbumSimplified> albumSimplifiedCacheAsMap = albumSimplifiedCache.asMap();
	ConcurrentMap<Object,Album> albumCacheAsMap = albumCache.asMap();
	ConcurrentMap<Object,Track> trackCacheAsMap = trackCache.asMap();
	
	
	org.apache.logging.log4j.Logger log = LogManager.getLogger(SearchItemMethods.class);
	
	
	
	
	public Artist cacheAndPutArtists (String name, ExternalUrl url, String imageUrl) {
			
			if (!artistCacheAsMap.containsKey(name)) {
				Artist artist = new Artist.Builder()
						.setName(name)
						.setExternalUrls(url)
						.setImages(new Image.Builder().setUrl(imageUrl).build())
						.setType(ModelObjectType.ARTIST)
						.build();
				artistCache.put(name, artist);
				log.info("Putting and getting from Arist cache");
				return artist;
				} else {
					log.info("Getting Directly and not putting from Artist cache");
					return artistCache.getIfPresent(name);
				}
		
	}
	
	public ArtistSimplified cacheAndPutArtistsSimplified (String name, ExternalUrl url) {
		
		if (!artistSimplifiedCacheAsMap.containsKey(name)) {
			ArtistSimplified artist = new ArtistSimplified.Builder()
					.setName(name)
					.setExternalUrls(url)
					.build();
			artistSimplifiedCache.put(name, artist);
			log.info("Putting and getting from Arist cache");
			return artist;
			} else {
				log.info("Getting Directly and not putting from Artist cache");
				return artistSimplifiedCache.getIfPresent(name);
			}
	
}
	
	public Album cacheAndPutAlbums (String name, ExternalUrl url,String imageUrl, 
			String artistName, ExternalUrl artistUrl, String artistImageUrl) {
		if (!albumCacheAsMap.containsKey(name)) {
			Album album =  new Album.Builder()
					.setName(name)
					.setExternalUrls(url)
					.setImages(new Image.Builder().setUrl(imageUrl).build())
					.setArtists( cacheAndPutArtistsSimplified(artistName, artistUrl))
					.build();
						albumCache.put(name, album);
						log.info("Putting and getting from Album cache");
					return album;
		} else {
			log.info("Getting Directly and not putting from Album Cache");
			return  albumCache.getIfPresent(name);
		}
		
	}
	
	public AlbumSimplified cacheAndPutAlbumsSimplified (String name, ExternalUrl url, String imageUrl, 
			String artistName, ExternalUrl artistUrl) {
		if (!albumCacheAsMap.containsKey(name)) {
			AlbumSimplified album =  new AlbumSimplified.Builder()
					.setName(name)
					.setExternalUrls(url)
					.setImages(new Image.Builder().setUrl(imageUrl).build())
					.setArtists( cacheAndPutArtistsSimplified(artistName, artistUrl))
					.build();
						albumSimplifiedCache.put(name, album);
						log.info("Putting and getting from Album cache");
					return album;
		} else {
			log.info("Getting Directly and not putting from Album Cache");
			return  albumSimplifiedCache.getIfPresent(name);
		}
		
	}
	
	public Track cacheAndPutTracks (String name, ExternalUrl url, String artistName, ExternalUrl artistUrl) {
		if (!trackCacheAsMap.containsKey(name)) {
			Track track = new Track.Builder()
					.setName(name)
					.setExternalUrls(url)
					.setArtists(cacheAndPutArtistsSimplified(artistName, artistUrl))				
					.build();
			log.info("Putting and getting from Track cache");
			return track;
			
		} else {
			log.info("Getting Directly and not putting from Track Cache");
			return trackCache.getIfPresent(name);
			
		}
		
	
	}
	 
	
	
	
}
