package com.jos.spotifyclone.model.album;


import org.springframework.stereotype.Component;

import com.wrapper.spotify.model_objects.specification.AlbumSimplified;


@Component
public class Albums {
	
	
	private AlbumSimplified[] items;

	public AlbumSimplified[] getItems() {
		return items;
	}

	public void setItems(AlbumSimplified[] items) {
		this.items = items;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public Albums(AlbumSimplified[] items) {
		super();
		this.items = items;
	}

	public Albums() {
	}
	
	
	
	
}
