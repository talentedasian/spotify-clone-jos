package com.jos.spotifyclone.controller;


import com.jos.spotifyclone.services.ComputeEtagValue;
import com.jos.spotifyclone.services.HttpHeadersResponse;
import com.jos.spotifyclone.services.SpotifyConnect;

import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequestMapping("api/user")
@RestController
public class UserController implements HttpHeadersResponse<Object>{


    private final SpotifyConnect spotifyConnect;
    private final WebRequest request;

    @Autowired
    public UserController (SpotifyConnect spotifyConnect, WebRequest request) {
    	this.spotifyConnect = spotifyConnect;
    	this.request = request;
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Object> currentUserProfile() throws ParseException, SpotifyWebApiException, IOException {
    	User response = spotifyConnect.getSpotifyApi().getCurrentUsersProfile().build().execute();
   
    	return responseEntity(response, null, HttpStatus.OK);
    }

    //http://localhost:8080/api/user/another-user?user_ids=provalio
    @GetMapping("/another-user")
    public ResponseEntity<Object> anotherUserProfile(@RequestParam String user_ids) throws ParseException, SpotifyWebApiException, IOException {
        if (request.checkNotModified(ComputeEtagValue.computeEtag(user_ids))) {
        	return null;
        }
    	
    	User response = spotifyConnect.getSpotifyApi().getUsersProfile(user_ids).build().execute();
        List<Object> anotherUserToResponse = new ArrayList<>();
        anotherUserToResponse.add(response);
        
        return responseEntity(anotherUserToResponse, user_ids, HttpStatus.OK);
    }

    @GetMapping("/playlist")
    public ResponseEntity<Object> playlistsOfCurrentUser() throws ParseException, SpotifyWebApiException, IOException {
    	
    	Paging<PlaylistSimplified> response = spotifyConnect.getSpotifyApi().getListOfCurrentUsersPlaylists().build().execute();
        List<PlaylistSimplified> listOfUsersPlaylistToResponse = new ArrayList<>();
        
        for (PlaylistSimplified listOfUsersPlaylist : response.getItems()){
            var listOfUsersPlaylistBuilder = new PlaylistSimplified.Builder()
            		.setName(listOfUsersPlaylist.getName())
            		.setId(listOfUsersPlaylist.getId())
            		.setImages(new Image.Builder()
            				.setUrl(listOfUsersPlaylist.getImages()[0].getUrl())
            				.build())
            		.setOwner(listOfUsersPlaylist.getOwner())
            		.build();
        	listOfUsersPlaylistToResponse.add(listOfUsersPlaylistBuilder);
        }

        return responseEntity(listOfUsersPlaylistToResponse, null, HttpStatus.OK);
    }
    

    @GetMapping("/followed-artists")
    public ResponseEntity<Object> followedArtists() throws ParseException, SpotifyWebApiException, IOException {
        PagingCursorbased<Artist> response = spotifyConnect.getSpotifyApi().getUsersFollowedArtists(ModelObjectType.ARTIST).build().execute();
        List<Object> followedArtistToResponse = new ArrayList<>();
        
        for (Artist followedArtist : response.getItems()) {
        	var artistBuilder = new Artist.Builder()
        			.setName(followedArtist.getName())
        			.setId(followedArtist.getId())
        			.setImages(new Image.Builder()
        					.setUrl(followedArtist.getImages()[0].getUrl())
        					.build())
        			.setFollowers(followedArtist.getFollowers())
        			.build();
        	
        	followedArtistToResponse.add(artistBuilder);
        }
        return responseEntity(followedArtistToResponse, null, HttpStatus.OK);
    }


    //http://localhost:8080/api/user/follow-playlist?playlistId=3AGOiaoRXMSjswCLtuNqv5
    @PostMapping()
    public String followPlaylist(@RequestParam String playlistId, @RequestParam(required = false) boolean public_) throws ParseException, SpotifyWebApiException, IOException {
        String response = spotifyConnect.getSpotifyApi().followPlaylist(playlistId, public_).build().execute();
        return "Success! You are now following " + playlistId + " playlist.";
    }

    //http://localhost:8080/api/user/unfollow-playlist?ownerId=abbaspotify&playlistId=3AGOiaoRXMSjswCLtuNqv5
    @GetMapping("/unfollow-playlist")
    public String unfollowPlaylist(String ownerId, @RequestParam String playlistId) throws ParseException, SpotifyWebApiException, IOException {
        String response = spotifyConnect.getSpotifyApi().unfollowPlaylist(ownerId, playlistId).build().execute();
        return "You are not following " + playlistId + " anymore.";
    }

    @GetMapping("/saved-albums")
    public ResponseEntity<Object> savedAlbums() throws ParseException, SpotifyWebApiException, IOException {
        Paging<SavedAlbum> response = spotifyConnect.getSpotifyApi().getCurrentUsersSavedAlbums().build().execute();
        List<Object> savedAlbumToResponse = new ArrayList<>();
        
        for(SavedAlbum savedAlbum : response.getItems()){
        	var album = savedAlbum.getAlbum();
        	for (ArtistSimplified savedAlbumArtist : savedAlbum.getAlbum().getArtists()) {
        		var savedAlbumBuilder = new SavedAlbum.Builder()
        				.setAddedAt(savedAlbum.getAddedAt())
        				.setAlbum(new Album.Builder()
        						.setName(album.getName())
        						.setId(album.getId())
        						.setImages(new Image.Builder()
        								.setUrl(album.getImages()[0].getUrl())
        								.build())
        						.setArtists(new ArtistSimplified.Builder()
        								.setName(savedAlbumArtist.getName())
        								.setId(savedAlbumArtist.getId())
        								.build())
        						.build())
						.build();
						
    		savedAlbumToResponse.add(savedAlbumBuilder);
        	}
        }
        return responseEntity(savedAlbumToResponse, null, HttpStatus.OK);
    }

    
    @GetMapping("/saved-tracks")
    public ResponseEntity<Object> savedTracks() throws ParseException, SpotifyWebApiException, IOException {
        Paging<SavedTrack> response = spotifyConnect.getSpotifyApi().getUsersSavedTracks().build().execute();
        
        List<Object> savedTrackToResponse = new ArrayList<>();
        for(SavedTrack savedTrack : response.getItems()){
        	var track = savedTrack.getTrack();
            for (ArtistSimplified savedTrackArtist : track.getArtists()) {
            	var savedTrackBuilder = new SavedTrack.Builder()
            			.setAddedAt(savedTrack.getAddedAt())
            			.setTrack(new Track.Builder()
            					.setName(track.getName())
            					.setId(track.getId())
            					.setAlbum(new AlbumSimplified.Builder()
            							.setImages(new Image.Builder()
            									.setUrl(track.getAlbum().getImages()[0].getUrl())
            									.build())
            							.build())
            					.setArtists(new ArtistSimplified.Builder()
            							.setName(savedTrackArtist.getName())
            							.setId(savedTrackArtist.getId())
            							.build())
            					.build())
            			.build();
            	
            	savedTrackToResponse.add(savedTrackBuilder);
            }
        }
        
        return responseEntity(savedTrackToResponse, null, HttpStatus.OK);
    }
//
//    //run http://localhost:8080/api/user/saved-albums/ first to find id's
//    //http://localhost:8080/api/user/remove-albums?ids=<replace with albums id>
//    @GetMapping("/remove-albums")
//    public String removeAlbums(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        String response = spotifyConnect.getSpotifyApi().removeAlbumsForCurrentUser(ids).build().execute();
//        return "Success! Album/s with id/s " + ids + "was/were deleted.";
//    }
//
//    //run http://localhost:8080/api/user/saved-shows/ first to find id's
//    //http://localhost:8080/api/user/remove-shows?ids=<replace with shows id>
//    @GetMapping("/remove-shows")
//    public String removeShows(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        String response = spotifyConnect.getSpotifyApi().removeUsersSavedShows(ids).build().execute();
//        return "Success! Show/s with id/s " + ids + "was/were deleted.";
//    }
//
//    //run http://localhost:8080/api/user/saved-tracks/ first to find id's
//    //http://localhost:8080/api/user/remove-tracks?ids=<replace with tracks id>
//    @GetMapping("/remove-tracks")
//    public String removeTracks(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        String response = spotifyConnect.getSpotifyApi().removeUsersSavedTracks(ids).build().execute();
//        return "Success! Track/s with id/s " + ids + "was/were deleted.";
//    }
//
//    //http://localhost:8080/api/search/album?id=arianagrande
//    //http://localhost:8080/api/user/save-albums?ids=<replace with albums id>
//    @GetMapping("/save-albums")
//    public String saveAlbums(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        String response = spotifyConnect.getSpotifyApi().saveAlbumsForCurrentUser(ids).build().execute();
//        return "Success! Album/s with id/s " + ids + "was/were saved.";
//    }
//
//    //http://localhost:8080/api/search/show?id=bieber
//    //http://localhost:8080/api/user/save-shows?ids=<replace with shows id>
//    @GetMapping("/save-shows")
//    public String saveShows(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        String response = spotifyConnect.getSpotifyApi().saveShowsForCurrentUser(ids).build().execute();
//        return "Success! Show/s with id/s " + ids + "was/were saved.";
//    }
//
//    //http://localhost:8080/api/search/track?id=positions
//    //http://localhost:8080/api/user/save-tracks?ids=<replace with tracks id>
//    @GetMapping("/save-tracks")
//    public String saveTracks(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        String response = spotifyConnect.getSpotifyApi().saveTracksForUser(ids).build().execute();
//        return "Success! Track/s with id/s " + ids + "was/were saved.";
//    }
//
//    //http://localhost:8080/api/user/check-saved-albums/?ids=66CXWjxzNUsdJxJ2JdwvnR
//    @GetMapping("/check-saved-albums")
//    public String checkSavedAlbums(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        Boolean[] response = spotifyConnect.getSpotifyApi().checkUsersSavedAlbums(ids).build().execute();
//        for (Boolean b : response){
//            if(b){
//                //regex removes square brackets with any content between them
//                return "You already have the " + Arrays.toString(ids).replaceAll("\\[(.*?)\\]", "$1" + " albums saved.");
//            }
//        }
//        return "You didn't save the " + Arrays.toString(ids).replaceAll("\\[(.*?)\\]", "$1" + " albums.");
//    }
//
//    //http://localhost:8080/api/user/check-saved-shows/?ids=0yGFanYUflGtxAN23HQLY2
//    @GetMapping("/check-saved-shows")
//    public String checkSavedShows(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        Boolean[] response = spotifyConnect.getSpotifyApi().checkUsersSavedShows(ids).build().execute();
//        for (Boolean b : response){
//            if(b){
//                //regex removes square brackets with any content between them
//                return "You already have the " + Arrays.toString(ids).replaceAll("\\[(.*?)\\]", "$1" + " shows saved.");
//            }
//        }
//        return "You didn't save the " + Arrays.toString(ids).replaceAll("\\[(.*?)\\]", "$1" + " shows.");
//
//    }
//
//    //http://localhost:8080/api/user/check-saved-tracks/?ids=66CXWjxzNUsdJxJ2JdwvnR
//    @GetMapping("/check-saved-tracks")
//    public String checkSavedTracks(@RequestParam String[] ids) throws ParseException, SpotifyWebApiException, IOException {
//        Boolean[] response = spotifyConnect.getSpotifyApi().checkUsersSavedTracks(ids).build().execute();
//        for (Boolean b : response){
//            if(b){
//                //regex removes square brackets with any content between them
//                return "You already have the " + Arrays.toString(ids).replaceAll("\\[(.*?)\\]", "$1" + " tracks saved.");
//            }
//        }
//        return "You didn't save the " + Arrays.toString(ids).replaceAll("\\[(.*?)\\]", "$1" + " tracks.");
//    }

    @GetMapping("/top-artists")
    public ResponseEntity<Object> getUsersTopArtists() throws ParseException, SpotifyWebApiException, IOException {
        Paging<Artist> response = spotifyConnect.getSpotifyApi().getUsersTopArtists().build().execute();

        List<Artist> usersTopArtistsToResponse = new ArrayList<>();
        for(Artist usersTopArtists : response.getItems()){
    		 var usersTopArtistsBuilder = new Artist.Builder()
    				 .setName(usersTopArtists.getName())
    				 .setId(usersTopArtists.getId())
    				 .setGenres(usersTopArtists.getGenres())
    				 .setImages(usersTopArtists.getImages())
    				 .build();
    		 
    		 usersTopArtistsToResponse.add(usersTopArtistsBuilder);
        }

        return responseEntity(usersTopArtistsToResponse, null, HttpStatus.OK);
    }


    @GetMapping("/top-tracks")
    public ResponseEntity<Object> getTopTracks() throws ParseException, SpotifyWebApiException, IOException {
        Paging<Track> response = spotifyConnect.getSpotifyApi().getUsersTopTracks().build().execute();

        List<Track> usersTopTracksToResponse = new ArrayList<>();
        for (Track usersTopTracks : response.getItems()) {
        	var usersTopTracksBuilder = new Track.Builder()
        			.setName(usersTopTracks.getName())
        			.setId(usersTopTracks.getId())
        			.setDurationMs(usersTopTracks.getDurationMs())
        			.setAlbum(new AlbumSimplified.Builder()
        						.setImages(usersTopTracks.getAlbum().getImages())
        						.build())
        			.build();
        			
        	usersTopTracksToResponse.add(usersTopTracksBuilder);			
        }
        return responseEntity(usersTopTracksToResponse, null, HttpStatus.OK);
    }

    //TODO fix so it works with getUsersTopArtistsAndTracks method
    @GetMapping("/top-artists-and-tracks")
    public ResponseEntity<Object> getUsersTopArtist() throws ParseException, SpotifyWebApiException, IOException {
        Paging<Artist> responseArtist = spotifyConnect.getSpotifyApi().getUsersTopArtists().build().execute();
        List<Artist> usersTopArtistToResponse = new ArrayList<>();
        
        for(Artist artist : responseArtist.getItems()){
        	var usersTopArtistBuilder = new Artist.Builder()
        			.setName(artist.getName())
        			.setId(artist.getId())
        			.setImages(new Image.Builder()
        					.setUrl(artist.getImages()[0].getUrl())
        					.build())
        			.setPopularity(artist.getPopularity())
        			.build();
        	
        	usersTopArtistToResponse.add(usersTopArtistBuilder);
        }
        return responseEntity(usersTopArtistToResponse, null, HttpStatus.OK);
    }

	@Override
	public ResponseEntity<Object> responseEntity(Object body,
			String appendingValue, HttpStatus status) {
		var headers = new HttpHeaders();
		
		if (appendingValue == null) {
			headers.setCacheControl(CacheControl.noStore());
			return new ResponseEntity<Object>(body, headers, status);
		}
		
		headers.setCacheControl(CacheControl.maxAge(86400L, TimeUnit.SECONDS).cachePrivate());
		headers.setETag("\"" + ComputeEtagValue.computeEtag(appendingValue) + "\"");
		
		return new ResponseEntity<Object>(body, headers, status);
	}

}


