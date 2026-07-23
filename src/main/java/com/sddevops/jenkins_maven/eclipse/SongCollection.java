package com.sddevops.jenkins_maven.eclipse;

import java.util.*;
import java.io.*;
import java.net.*;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.logging.Level;


public class SongCollection {

    private ArrayList<Song> songs = new ArrayList<>();
    private int capacity;
    private LocalDateTime timeCreated;

    public SongCollection() {
        this.capacity = 20;
        this.timeCreated = LocalDateTime.now(); //NOSONAR
    }

    public SongCollection(int capacity) {
        this.capacity = capacity;
        this.timeCreated = LocalDateTime.now(); //NOSONAR
    }

    public List<Song> getSongs() {
        return songs;
    }

    public String getYearCreated() {
        return String.valueOf(this.timeCreated.getYear());
    }
    
    public String getFullDateCreated() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return this.timeCreated.format(formatter);
    }
    
    public String compareCollection(SongCollection other) {
        if (this.timeCreated.isBefore(other.timeCreated)) {
            return "My collection is older!";
        } else if (this.timeCreated.isEqual(other.timeCreated)) {
            return "My collection was created at the same time!";
        } else {
            return "My collection is newer!";
        }
    }
    
    public void addSong(Song song) {
    	if(songs.size() != capacity) {
    		songs.add(song);
    	}
    }
    
    public List<Song> sortSongsByTitle() {
        Collections.sort(songs, Song.titleComparator);
        return songs;
    }
    
    public List<Song> sortSongsBySongLength() {
        Collections.sort(songs, Song.songLengthComparator);
        return songs;
    }
    
    public Song findSongsById(String id) {
    	for (Song s : songs) { 		      
            if(s.getId().equals(id)) return s;
       }
    	return null;
    }

    public Song findSongByTitle(String title) {
    	for (Song s : songs) { 		      
            if(s.getTitle().equals(title)) return s;
       }
    	return null;
    }
    
    // Cleaned up method layout
    protected String fetchSongJson() {
        String urlString = "https://mocki.io/v1/e1b14dea-d272-4b03-b102-252325168182";
        // Initialize the logger for this class
        Logger logger = Logger.getLogger(SongCollection.class.getName());
        
        try {
            HttpURLConnection conn = openConnection(urlString);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            // FIXED: Replaced e.printStackTrace() with a proper structural logger
            logger.log(Level.SEVERE, "Failed to fetch song JSON", e);
        }
        return null;
    }
    
 // FIXED: Replaced generic 'throws Exception' with dedicated IO exceptions
    protected HttpURLConnection openConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        return (HttpURLConnection) url.openConnection();
    }
    
    public Song fetchSongOfTheDay() {
    	Logger logger = Logger.getLogger(SongCollection.class.getName());
        try {
            String jsonStr = fetchSongJson();
            if (jsonStr == null) return null;

            JSONObject json = new JSONObject(jsonStr);

            Song song = new Song(
                json.getString("id"),
                json.getString("title"),
                json.getString("artiste"),
                json.getDouble("songLength")
            );
            
            if (song.getArtiste().equals("Taylor Swift")) {
                song.setArtiste("TS");
                this.addSong(song);
            } else if (song.getArtiste().equals("Bruno Mars")) {
                song.setArtiste("BM");
                this.addSong(song);
            }

            return song;

        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Error parsing or processing song of the day", e);
            return null;
        }
    }
}