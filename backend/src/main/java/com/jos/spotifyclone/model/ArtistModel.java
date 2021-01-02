package com.jos.spotifyclone.model;

import com.wrapper.spotify.model_objects.specification.ExternalUrl;
import com.wrapper.spotify.model_objects.specification.Followers;
import com.wrapper.spotify.model_objects.specification.Image;

public class ArtistModel {
    ExternalUrl externalUrl;
    Followers followers;
    String[] genres;
    Image[] images;
    String artistName;
	
    public ArtistModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ArtistModel(ExternalUrl externalUrl, Followers followers, String[] genres, Image[] images, String artistName) {
		super();
		this.externalUrl = externalUrl;
		this.followers = followers;
		this.genres = genres;
		this.images = images;
		this.artistName = artistName;
	}

	public ExternalUrl getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(ExternalUrl externalUrl) {
		this.externalUrl = externalUrl;
	}

	public Followers getFollowers() {
		return followers;
	}

	public void setFollowers(Followers followers) {
		this.followers = followers;
	}

	public String[] getGenres() {
		return genres;
	}

	public void setGenres(String[] genres) {
		this.genres = genres;
	}

	public Image[] getImages() {
		return images;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	
	
    
    
    

    
}