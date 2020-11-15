package com.jos.spotifyclone.model.album;


import java.util.List;

import org.springframework.stereotype.Component;

import com.wrapper.spotify.model_objects.specification.AlbumSimplified;


@Component
public class Albums {
	
	
	private List<Items>items;

	public List<Items> getItems() {
		return items;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public Albums(List<Items> items) {
		super();
		this.items = items;
	}

	public Albums() {
	}
	
	
	
	
}
