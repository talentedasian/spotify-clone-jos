package com.jos.spotifyclone.services;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;

@Component
public class SearchItemMethods {
	
	com.github.benmanes.caffeine.cache.Cache<String, ArtistSimplified> artistSimplifiedCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<String, Artist> artistCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<String, AlbumSimplified> albumSimplifiedCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<String, Track> trackCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	com.github.benmanes.caffeine.cache.Cache<String, PlaylistSimplified> playlistSimplifiedCache = Caffeine.newBuilder().initialCapacity(10)
			.maximumSize(150).expireAfterAccess(60, TimeUnit.SECONDS).recordStats().build();
	
	org.apache.logging.log4j.Logger log = LogManager.getLogger(SearchItemMethods.class);
	
	public Artist cacheAndPutArtists (String name, String id, Image[] images) {
			if (!artistCache.asMap().containsKey(name)) {
				Artist artist = new Artist.Builder()
						.setName(name)
						.setId(id)
						.setImages(images)
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
	
	public ArtistSimplified cacheAndPutArtistsSimplified (String name, String id) {
		if (!artistSimplifiedCache.asMap().containsKey(name)) {
			ArtistSimplified artist = new ArtistSimplified.Builder()
					.setName(name)
					.setId(id)
					.build();
			artistSimplifiedCache.put(name, artist);
			log.info("Putting and getting from ArtistSimplified cache");
			return artist;
			} else {
				log.info("Getting Directly and not putting from ArtistSimplified cache");
				return artistSimplifiedCache.getIfPresent(name);
			}
	}
	
	public AlbumSimplified cacheAndPutAlbumsSimplified (String name, String id, Image[] images, 
			String artistName, String artistId) {
		if (!albumSimplifiedCache.asMap().containsKey(name)) {
			AlbumSimplified album =  new AlbumSimplified.Builder()
					.setName(name)
					.setId(id)
					.setImages(images)
					.setArtists(cacheAndPutArtistsSimplified(artistName, artistId))
					.setType(ModelObjectType.ALBUM)
					.build();
						albumSimplifiedCache.put(name, album);
						log.info("Putting and getting from AlbumSimplified cache");
					return album;
		} else {
			log.info("Getting Directly and not putting from AlbumSimplified Cache");
			return albumSimplifiedCache.getIfPresent(name);
		}
	}
	
	public Track cacheAndPutTracks (String name, String id, String artistName, String artistId, Image[] images) {
		if (!trackCache.asMap().containsKey(name)) {
			Track track = new Track.Builder()
					.setName(name)
					.setId(id)
					.setArtists(cacheAndPutArtistsSimplified(artistName, artistId))		
					.setAlbum(new AlbumSimplified.Builder()
							.setImages(images)
							.build())
					.setType(ModelObjectType.TRACK)
					.build();
			trackCache.put(name, track);
			log.info("Putting and getting from Track cache");
			return track;
			
		} else {
			log.info("Getting Directly and not putting from Track Cache");
			return trackCache.getIfPresent(name);
		}
	}
	
	public PlaylistSimplified cacheAndPutPlaylistsSimplified (String name, String id, User owner,Image[] images) {
		if (!playlistSimplifiedCache.asMap().containsKey(name)) {
			PlaylistSimplified playlist = new PlaylistSimplified.Builder()
					.setName(name)
					.setId(id)
					.setOwner(owner)
					.setImages(images)
					.setType(ModelObjectType.PLAYLIST)
					.build();
			playlistSimplifiedCache.put(name, playlist);
			log.info("Putting and getting from PlaylistSimplified cache");
			return playlist;
		} else {
			log.info("Getting Directly and not putting from Playlist cache");
			return playlistSimplifiedCache.getIfPresent(name);
		}
	}
	
}
