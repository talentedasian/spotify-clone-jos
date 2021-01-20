package com.jos.spotifyclone.controller;


import com.jos.spotifyclone.model.*;
import com.jos.spotifyclone.services.ComputeEtagValue;
import com.jos.spotifyclone.services.HttpHeadersResponse;
import com.jos.spotifyclone.services.SpotifyConnect;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.PlaylistTracksInformation;
import com.wrapper.spotify.model_objects.specification.*;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequestMapping("api/user")
@RestController
public class UserController implements HttpHeadersResponse<Map<String,List<Object>>>{


    private final SpotifyConnect spotifyConnect;
    private final WebRequest request;

    @Autowired
    public UserController (SpotifyConnect spotifyConnect, WebRequest request) {
    	this.spotifyConnect = spotifyConnect;
    	this.request = request;
    }
    
    @GetMapping("/profile")
    public User currentUserProfile() throws ParseException, SpotifyWebApiException, IOException {
    	User response = spotifyConnect.getSpotifyApi().getCurrentUsersProfile().build().execute();
    	
    	return response;
    }

    //http://localhost:8080/api/user/another-user?user_ids=provalio
    @GetMapping("/another-user")
    public ResponseEntity<Map<String,List<Object>>> anotherUserProfile(@RequestParam String user_ids) throws ParseException, SpotifyWebApiException, IOException {
        if (request.checkNotModified(ComputeEtagValue.computeEtag(user_ids))) {
        	return null;
        }
    	
    	User response = spotifyConnect.getSpotifyApi().getUsersProfile(user_ids).build().execute();
        Map<String,List<Object>> map = new HashMap<>();
        List<Object> anotherUserToResponse = new ArrayList<>();
        anotherUserToResponse.add(response);
        map.put("Another-User-Details", anotherUserToResponse);
        
        return responseEntity(map, user_ids, HttpStatus.OK);
    }

    @GetMapping("/playlist")
    public Map<String,Object> playlistsOfCurrentUser() throws ParseException, SpotifyWebApiException, IOException {
    	
    	Paging<PlaylistSimplified> response = spotifyConnect.getSpotifyApi().getListOfCurrentUsersPlaylists().build().execute();
        String href = response.getHref();

        List<PlaylistModel> list = new ArrayList<>();
        for (PlaylistSimplified playlist : response.getItems()){
            ExternalUrl externalUrls = playlist.getExternalUrls();
            String playlistName = playlist.getName();
            PlaylistTracksInformation tracks = playlist.getTracks();
            Image[] playlistCover = playlist.getImages();

            list.add(new PlaylistModel(href, externalUrls, playlistName, tracks, playlistCover));
        }

        Map<String,Object> map = new HashMap<>();
        map.put("User playlists", list);

        return map;
    }

    @GetMapping("/followed-artists")
    public Map<String,List<Object>> followedArtists() throws ParseException, SpotifyWebApiException, IOException {
        PagingCursorbased<Artist> response = spotifyConnect.getSpotifyApi().getUsersFollowedArtists(ModelObjectType.ARTIST).build().execute();
        Map<String,List<Object>> map = new HashMap<>();
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
        map.put("User-Followed-Artist", followedArtistToResponse);
        return map;
    }

//    //http://localhost:8080/api/user/check-follow-artist-user?user_ids=1l0mKo96Jh9HVYONcRl3Yp
//    @GetMapping("/check-follow-artist-user")
//    public String checkFollowArtistOrUser(@RequestParam String[] user_ids) throws ParseException, SpotifyWebApiException, IOException {
//        final ModelObjectType type = ModelObjectType.ARTIST;
//        Boolean[] response = spotifyConnect.getSpotifyApi().checkCurrentUserFollowsArtistsOrUsers(type,user_ids).build().execute();
//        for (Boolean b : response){
//            if(b){
//                //regex removes square brackets with any content between them
//                return "You are already following " + Arrays.toString(user_ids).replaceAll("\\[(.*?)\\]", "$1");
//            }
//        }
//        return "You are not following " + Arrays.toString(user_ids).replaceAll("\\[(.*?)\\]", "$1");
//    }
//
//    //http://localhost:8080/api/user/follow-artist-user?user_ids=1l0mKo96Jh9HVYONcRl3Yp
//    @GetMapping("/follow-artist-user")
//    public String followArtistOrUser(@RequestParam String[] user_ids) throws ParseException, SpotifyWebApiException, IOException {
//        final ModelObjectType type = ModelObjectType.ARTIST;
//        String response = spotifyConnect.getSpotifyApi().followArtistsOrUsers(type,user_ids).build().execute();
//        Boolean[] responseCheck = spotifyConnect.getSpotifyApi().checkCurrentUserFollowsArtistsOrUsers(type,user_ids).build().execute();
//        for (Boolean b : responseCheck){
//            if(b){
//                //regex removes square brackets with any content between them
//                return "Success! You are now following " + Arrays.toString(user_ids).replaceAll("\\[(.*?)\\]", "$1");
//            }
//        }
//        return "You are not following " + Arrays.toString(user_ids).replaceAll("\\[(.*?)\\]", "$1");
//    }
//
//    //http://localhost:8080/api/user/unfollow-artist-user?user_ids=1l0mKo96Jh9HVYONcRl3Yp
//    @GetMapping("/unfollow-artist-user")
//    public String unfollowArtistOrUser(@RequestParam String[] user_ids) throws ParseException, SpotifyWebApiException, IOException {
//        final ModelObjectType type = ModelObjectType.ARTIST;
//        String response = spotifyConnect.getSpotifyApi().unfollowArtistsOrUsers(type,user_ids).build().execute();
//        Boolean[] responseCheck = spotifyConnect.getSpotifyApi().checkCurrentUserFollowsArtistsOrUsers(type,user_ids).build().execute();
//        for (Boolean b : responseCheck){
//            if(b){
//                //regex removes square brackets with any content between them
//                return "Success! You are now following " + Arrays.toString(user_ids).replaceAll("\\[(.*?)\\]", "$1");
//            }
//        }
//        return "You are not following " + Arrays.toString(user_ids).replaceAll("\\[(.*?)\\]", "$1") + " anymore.";
//    }
//
//    //http://localhost:8080/api/user/check-follow-playlist?ownerId=abbaspotify&playlistId=3AGOiaoRXMSjswCLtuNqv5&user_ids=abbaspotify
//    @GetMapping("/check-follow-playlist")
//    public String checkUsersFollowPlaylist(@RequestParam String ownerId, @RequestParam String playlistId, @RequestParam String[] user_ids) throws ParseException, SpotifyWebApiException, IOException {
//        Boolean[] response = spotifyConnect.getSpotifyApi().checkUsersFollowPlaylist(ownerId, playlistId, user_ids).build().execute();
//        for (Boolean b : response){
//            if(b){
//                return "The users are following " + playlistId.replaceAll("\\[(.*?)\\]", "$1");
//            }
//        }
//        return "The users are not following " + playlistId.replaceAll("\\[(.*?)\\]", "$1");
//    }
//
//    //http://localhost:8080/api/user/follow-playlist?playlistId=3AGOiaoRXMSjswCLtuNqv5
//    @GetMapping("/follow-playlist")
//    public String followPlaylist(@RequestParam String playlistId, @RequestParam(required = false) boolean public_) throws ParseException, SpotifyWebApiException, IOException {
//        String response = spotifyConnect.getSpotifyApi().followPlaylist(playlistId, public_).build().execute();
//        return "Success! You are now following the " + playlistId + " playlist.";
//    }
//
//    //http://localhost:8080/api/user/unfollow-playlist?ownerId=abbaspotify&playlistId=3AGOiaoRXMSjswCLtuNqv5
//    @GetMapping("/unfollow-playlist")
//    public String unfollowPlaylist(String ownerId, @RequestParam String playlistId) throws ParseException, SpotifyWebApiException, IOException {
//        String response = spotifyConnect.getSpotifyApi().unfollowPlaylist(ownerId, playlistId).build().execute();
//        return "You are not following " + playlistId + " anymore.";
//    }

    @GetMapping("/saved-albums")
    public Map<String, Object> savedAlbums() throws ParseException, SpotifyWebApiException, IOException {
        Paging<SavedAlbum> response = spotifyConnect.getSpotifyApi().getCurrentUsersSavedAlbums().build().execute();
        Map<String, Object> map = new HashMap<>();
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
        map.put("User-Saved-Album", savedAlbumToResponse);
        return map;
    }

//    @GetMapping("/saved-tracks")
//    public Map<String, Object> savedTracks() throws ParseException, SpotifyWebApiException, IOException {
//        Paging<SavedTrack> response = spotifyConnect.getSpotifyApi().getUsersSavedTracks().build().execute();
//        Map<String, Object> map = new HashMap<>();
//        List<Object> savedTrackToResponse = new ArrayList<>();
//        
//        for(SavedTrack savedTrack : response.getItems()){
//        	var track = savedTrack.getTrack();
//            for (ArtistSimplified savedTrackArtist : track.getArtists()) {
//            	var savedTrackBuilder = new SavedTrack.Builder()
//            			.setAddedAt(savedTrack.getAddedAt())
//            			.setTrack(new Track.Builder()
//            					.setName(track.getName())
//            					.setId(track.getId())
//            					.setAlbum(new AlbumSimplified.Builder()
//            							.setImages(new Image.Builder()
//            									.setUrl(track.getAlbum().getImages()[0].getUrl())
//            									.build())
//            							.build())
//            					.setArtists(new ArtistSimplified.Builder()
//            							.setName(savedTrackArtist.getName())
//            							.setId(savedTrackArtist.getId())
//            							.build())
//            					.build())
//            			.build();
//            	
//            	savedTrackToResponse.add(savedTrackBuilder);
//            }
//        }
//        map.put("User-Saved-Track", savedTrackToResponse);
//        return map;
//    }
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
    public Map<String, Object> getTopArtists() throws ParseException, SpotifyWebApiException, IOException {
        var response = spotifyConnect.getSpotifyApi().getUsersTopArtists().build().execute();

        List<ArtistModel> list = new ArrayList<>();
        for(Artist artist : response.getItems()){
            ExternalUrl externalUrl = artist.getExternalUrls();
            Followers followers = artist.getFollowers();
            String[] genres = artist.getGenres();
            Image[] images = artist.getImages();
            String artistName = artist.getName();

            list.add(new ArtistModel(externalUrl, followers, genres, images, artistName));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("Top artists ", list);
        return map;
    }


    @GetMapping("/top-tracks")
    public Map<String, Object> getTopTracks() throws ParseException, SpotifyWebApiException, IOException {
        var response = spotifyConnect.getSpotifyApi().getUsersTopTracks().build().execute();

        List<TrackModel> list = new ArrayList<>();
        for(Track track : response.getItems()){
            String name = track.getName();
            ExternalUrl externalUrls = track.getExternalUrls();

            List<String> artistsList = new ArrayList<>();
            ArtistSimplified[] artists = track.getArtists();
            for(ArtistSimplified artistSimplified : artists){
                artistsList.add(artistSimplified.getName());
            }

            List<AlbumModel> albumsList = new ArrayList<>();
            AlbumSimplified albums = track.getAlbum();
            String nameAlbum = albums.getName();
            Image[] imageAlbum = albums.getImages();
            ExternalUrl externalUrlAlbum = albums.getExternalUrls();
            List<String> artistsOfAlbumList = new ArrayList<>();
            ArtistSimplified[] artistsOfAlbum = albums.getArtists();
            for(ArtistSimplified artistSimplified : artistsOfAlbum){
                artistsOfAlbumList.add(artistSimplified.getName());
            }
            albumsList.add(new AlbumModel(nameAlbum, artistsOfAlbumList, imageAlbum, externalUrlAlbum));

            list.add(new TrackModel(name, externalUrls, artistsList, albumsList));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("Top tracks ", list);
        return map;
    }

    //TODO fix so it works with getUsersTopArtistsAndTracks method
    @GetMapping("/top-artists-and-tracks")
    public Map<String, Object> getTopArtistsAndTracks() throws ParseException, SpotifyWebApiException, IOException {
        var responseArtist = spotifyConnect.getSpotifyApi().getUsersTopArtists().build().execute();
        List<ArtistModel> artistModels = new ArrayList<>();
        for(Artist artist : responseArtist.getItems()){
            ExternalUrl externalUrl = artist.getExternalUrls();
            Followers followers = artist.getFollowers();
            String[] genres = artist.getGenres();
            Image[] images = artist.getImages();
            String artistName = artist.getName();

            artistModels.add(new ArtistModel(externalUrl, followers, genres, images, artistName));
        }

        var responseTracks = spotifyConnect.getSpotifyApi().getUsersTopTracks().build().execute();
        List<TrackModel> trackModels = new ArrayList<>();
        for(Track track : responseTracks.getItems()){
            String name = track.getName();
            ExternalUrl externalUrls = track.getExternalUrls();

            List<String> artistsList = new ArrayList<>();
            ArtistSimplified[] artistsTracks = track.getArtists();
            for(ArtistSimplified artistSimplified : artistsTracks){
                artistsList.add(artistSimplified.getName());
            }

            List<AlbumModel> albumsList = new ArrayList<>();
            AlbumSimplified albums = track.getAlbum();
            String nameAlbum = albums.getName();
            Image[] imageAlbum = albums.getImages();
            ExternalUrl externalUrlAlbum = albums.getExternalUrls();
            List<String> artistsOfAlbumList = new ArrayList<>();
            ArtistSimplified[] artistsOfAlbum = albums.getArtists();
            for(ArtistSimplified artistSimplified : artistsOfAlbum){
                artistsOfAlbumList.add(artistSimplified.getName());
            }
            albumsList.add(new AlbumModel(nameAlbum, artistsOfAlbumList, imageAlbum, externalUrlAlbum));

            trackModels.add(new TrackModel(name, externalUrls, artistsList, albumsList));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("Top artists", artistModels);
        map.put("Top tracks", trackModels);
        return map;
    }

    //http://localhost:8080/api/user/get-list-of-playlists-from?user_id=hrn1isdy2ia8q7wfb1ew2fah6
    @GetMapping("/get-list-of-playlists-from")
    public Map<String, Object> getListOfAnotherUserPlaylists(@RequestParam String user_id) throws ParseException, SpotifyWebApiException, IOException {
        var response = spotifyConnect.getSpotifyApi().getListOfUsersPlaylists(user_id).build().execute();

        List<PlaylistModel> list = new ArrayList<>();
        for(PlaylistSimplified playlist : response.getItems()){
            String href = playlist.getHref();
            ExternalUrl externalUrls = playlist.getExternalUrls();
            String playlistName = playlist.getName();
            PlaylistTracksInformation tracks = playlist.getTracks();
            Image[] playlistCover = playlist.getImages();

            list.add(new PlaylistModel(href, externalUrls, playlistName, tracks, playlistCover));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("Playlists ", list);
        return map;
    }

	@Override
	public ResponseEntity<Map<String, List<Object>>> responseEntity(Map<String, List<Object>> body,
			String appendingValue, HttpStatus status) {
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.maxAge(86400L, TimeUnit.SECONDS).cachePrivate());
		headers.setETag("\"" + ComputeEtagValue.computeEtag(appendingValue) + "\"");
		
		return new ResponseEntity<Map<String,List<Object>>>(body, headers, status);
	}
}


