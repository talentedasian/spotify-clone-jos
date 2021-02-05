package com.jos.spotifyclone.services;


import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;

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
	
	org.apache.logging.log4j.Logger log = LogManager.getLogger(SearchItemMethods.class);
	
	public Artist cacheAndPutArtists (String name, String id, Image[] images) {
		Artist artist = new Artist.Builder()
				.setName(name)
				.setId(id)
				.setImages(images)
				.setType(ModelObjectType.ARTIST)
				.build();
		return artist;
	}
	
	public ArtistSimplified cacheAndPutArtistsSimplified (String name, String id) {
		ArtistSimplified artist = new ArtistSimplified.Builder()
				.setName(name)
				.setId(id)
				.build();
		return artist;
	}
	
	public AlbumSimplified cacheAndPutAlbumsSimplified (String name, String id, Image[] images, 
			String artistName, String artistId) {
		AlbumSimplified album =  new AlbumSimplified.Builder()
				.setName(name)
				.setId(id)
				.setImages(images)
				.setArtists(cacheAndPutArtistsSimplified(artistName, artistId))
				.setType(ModelObjectType.ALBUM)
				.build();
		return album;
	}
	
	public Track cacheAndPutTracks (String name, String id, String artistName, String artistId, Image[] images) {
		Track track = new Track.Builder()
				.setName(name)
				.setId(id)
				.setArtists(cacheAndPutArtistsSimplified(artistName, artistId))		
				.setAlbum(new AlbumSimplified.Builder()
						.setImages(images)
						.build())
				.setType(ModelObjectType.TRACK)
				.build();
		return track;
	}
	
	public PlaylistSimplified cacheAndPutPlaylistsSimplified (String name, String id, User owner,Image[] images) {
		PlaylistSimplified playlist = new PlaylistSimplified.Builder()
				.setName(name)
				.setId(id)
				.setOwner(owner)
				.setImages(images)
				.setType(ModelObjectType.PLAYLIST)
				.build();
		return playlist;
	}
	
}
