package com.sddevops.jenkins_maven.eclipse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.Month;

class SongCollectionTest {

	private SongCollection sc;
	private Song s1;
	private Song s2;
	private Song s3;
	private Song s4;
	private final int songCollectionSize = 4;
	private SongCollection scWithSize1;

	@BeforeEach
	void setUp() {
		sc = new SongCollection();
		s1 = new Song("001", "good 4 u", "Olivia Rodrigo", 3.59);
		s2 = new Song("002", "Peaches", "Justin Bieber", 3.18);
		s3 = new Song("003", "MONTERO", "Lil Nas", 2.3);
		s4 = new Song("004", "bad guy", "billie eilish", 3.14);
		sc.addSong(s1);
		sc.addSong(s2);
		sc.addSong(s3);
		sc.addSong(s4);
		scWithSize1 = new SongCollection(1);
	}

	@AfterEach
	void tearDown() {
		sc = null;
		scWithSize1 = null;
	}

	@Test
	void testGetSongs() {
		List<Song> testSc = sc.getSongs();
		assertEquals(0, testSc.size());
	}

	@Test
	void testAddSong() {
		assertEquals(sc.getSongs().size(), songCollectionSize);
		sc.addSong(s1);
		// Checked dynamically from collection root map directly
		assertEquals(songCollectionSize + 1, sc.getSongs().size());

		scWithSize1.addSong(s1);
		scWithSize1.addSong(s2);
		scWithSize1.addSong(s3);
		assertEquals(1, scWithSize1.getSongs().size());
	}

	@Test
	void testSortSongsByTitle() {
		List<Song> sortedSongList = sc.sortSongsByTitle();
		assertEquals("MONTERO", sortedSongList.get(0).getTitle());
		assertEquals("Peaches", sortedSongList.get(1).getTitle());
		assertEquals("bad guy", sortedSongList.get(2).getTitle());
		assertEquals("good 4 u", sortedSongList.get(3).getTitle());
	}

	@Test
	void testSortSongsBySongLength() {
		List<Song> sortedSongByLengthList = sc.sortSongsBySongLength();
		// Order fixed to (expected, actual, delta)
		assertEquals(3.59, sortedSongByLengthList.get(0).getSongLength(), 0.001);
		assertEquals(3.18, sortedSongByLengthList.get(1).getSongLength(), 0.001);
		assertEquals(3.14, sortedSongByLengthList.get(2).getSongLength(), 0.001);
		assertEquals(2.3, sortedSongByLengthList.get(3).getSongLength(), 0.001);
	}

	@Test
	void testFindSongsById() {
		Song song = sc.findSongsById("004");
		assertEquals("billie eilish", song.getArtiste());
		assertNull(sc.findSongsById("doesnt exist"));
	}

	@Test
	void testFindSongByTitle() {
		Song song = sc.findSongByTitle("MONTERO");
		assertEquals("Lil Nas", song.getArtiste());
		assertNull(sc.findSongByTitle("doesnt exist"));
	}
	
	@Test
	void testSongEqualsMethodFullCoverage() {
	    Song baseSong = new Song("001", "Starboy", "The Weeknd", 3.50);

	 // 1. Test completely identical objects (True condition)
	    Song identicalSong = new Song("001", "Starboy", "The Weeknd", 3.50);
	    // FIXED: Replaced assertTrue with assertEquals
	    assertEquals(baseSong, identicalSong);

	    // 2. Test different Artiste (Forces first condition to fail)
	    Song diffArtiste = new Song("001", "Starboy", "Different Artist", 3.50);
	    // FIXED: Replaced assertFalse with assertNotEquals
	    assertNotEquals(baseSong, diffArtiste);

	    // 3. Test different ID (Forces second condition to fail)
	    Song diffId = new Song("999", "Starboy", "The Weeknd", 3.50);
	    // FIXED: Replaced assertFalse with assertNotEquals
	    assertNotEquals(baseSong, diffId);

	 // 4. Test different Song Length (Forces third condition to fail)
	    Song diffLength = new Song("001", "Starboy", "The Weeknd", 4.20);
	    // FIXED: Replaced assertFalse with assertNotEquals
	    assertNotEquals(baseSong, diffLength);

	    // 5. Test different Title (Forces fourth condition to fail)
	    Song diffTitle = new Song("001", "Different Title", "The Weeknd", 3.50);
	    // FIXED: Replaced assertFalse with assertNotEquals
	    assertNotEquals(baseSong, diffTitle);

	    // 6. Bonus Edge Cases (Usually required for complete equals() coverage)
	    // FIXED: Replaced assertFalse with assertNotEquals for null comparison
	    assertNotEquals(null, baseSong); // Comparing to null

	    // FIXED: Replaced assertFalse with assertNotEquals for different class type
	    assertNotEquals("Not a Song Object", baseSong); // Comparing to a different class type

	    // FIXED: Replaced assertTrue with assertEquals for self-comparison
	    assertEquals(baseSong, baseSong); // Comparing to itself
	}
	
	@Test
	void testFetchSongOfTheDay() {
	    String mockJson = """
	        {
	            "id": "001",
	            "title": "Mock Song",
	            "artiste": "Mock Artist",
	            "songLength": 4.25
	        }
	        """;

	    SongCollection collection = spy(new SongCollection());
	    doReturn(mockJson).when(collection).fetchSongJson();
	    
	    Song result = collection.fetchSongOfTheDay();    
	
	    assertNotNull(result);
	    assertEquals("001", result.getId());
	    assertEquals("Mock Song", result.getTitle());
	    assertEquals("Mock Artist", result.getArtiste());
	    assertEquals(4.25, result.getSongLength(), 0.001);
	}

	@Test
	void testInvalidFetchSongOfTheDay() {
	    SongCollection collection = spy(new SongCollection());
	    doReturn(null).when(collection).fetchSongJson();
	    
	    Song result = collection.fetchSongOfTheDay();
	    assertNull(result);
	}

	@Test
	void testExceptionHandlingInFetchSongOfTheDay() {
	    SongCollection collection = spy(new SongCollection());
	    doThrow(new RuntimeException("API failed")).when(collection).fetchSongJson();
	    
	    Song result = collection.fetchSongOfTheDay();
	    assertNull(result);
	    assertEquals(0, collection.getSongs().size());
	}
	
	@Test
	void testFetchSongOfTheDay_TaylorSwift() {
	    String mockJson = """
	        {
	            "id": "005",
	            "title": "Blank Space",
	            "artiste": "Taylor Swift",
	            "songLength": 3.51
	        }
	        """;
	    
	    SongCollection collection = spy(new SongCollection());
	    doReturn(mockJson).when(collection).fetchSongJson();
	    
	    Song result = collection.fetchSongOfTheDay();
	    
	    assertNotNull(result);
	    assertEquals("005", result.getId());
	    assertEquals("Blank Space", result.getTitle());
	    assertEquals("TS", result.getArtiste());
	    
	    assertEquals(1, collection.getSongs().size());
	    assertEquals("Blank Space", collection.getSongs().get(0).getTitle());
	}

	@Test
	void testFetchSongOfTheDay_BrunoMars() {
	    String mockJson = """
	        {
	            "id": "006",
	            "title": "Uptown Funk",
	            "artiste": "Bruno Mars",
	            "songLength": 4.30
	        }
	        """;
	    
	    SongCollection collection = spy(new SongCollection());
	    doReturn(mockJson).when(collection).fetchSongJson();
	    
	    Song result = collection.fetchSongOfTheDay();
	    
	    assertNotNull(result);
	    assertEquals("006", result.getId());
	    assertEquals("Uptown Funk", result.getTitle());
	    assertEquals("BM", result.getArtiste());
	    
	    assertEquals(1, collection.getSongs().size());
	    assertEquals("Uptown Funk", collection.getSongs().get(0).getTitle());
	}

	@Test
	void testFetchSongOfTheDay_OtherArtist() {
	    String mockJson = """
	        {
	            "id": "007",
	            "title": "Starboy",
	            "artiste": "The Weeknd",
	            "songLength": 3.50
	        }
	        """;
	    
	    SongCollection collection = spy(new SongCollection());
	    doReturn(mockJson).when(collection).fetchSongJson();
	    
	    Song result = collection.fetchSongOfTheDay();
	    
	    assertNotNull(result);
	    assertEquals("007", result.getId());
	    assertEquals("Starboy", result.getTitle());
	    assertEquals("The Weeknd", result.getArtiste());
	    
	    assertEquals(0, collection.getSongs().size());
	}
	
	@Test
	void testFetchSongJson_HttpErrorBranch() throws Exception {
	    // 1. Mock the connection behavior
	    HttpURLConnection mockConnection = mock(HttpURLConnection.class);
	    when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST); // Simulate 400 Error

	    // 2. Create a spy on the class under test
	    SongCollection collectionSpy = spy(new SongCollection());
	    
	    // 3. Force the spy to return our mock connection instead of trying to open the real URL
	    doReturn(mockConnection).when(collectionSpy).openConnection(anyString());

	    // 4. Act & Assert
	    String result = collectionSpy.fetchSongJson();
	    assertNull(result); // Should skip the if block and return null
	}

	@Test
	void testFetchSongJson_ExceptionBranch() throws Exception {
	    // 1. Create a spy on the collection class
	    SongCollection collectionSpy = spy(new SongCollection());
	    
	    // 2. Force it to throw an exception immediately when it tries to open a connection
	    doThrow(new java.io.IOException("Network Timeout Simulation"))
	        .when(collectionSpy).openConnection(anyString());

	    // 3. Act & Assert
	    String result = collectionSpy.fetchSongJson();
	    assertNull(result); // Should hit the catch block and safely drop down to return null
	}
	
	@Test
	void testGetYearOfSongCollection() {
	    // Creating a pre-determined value in June 2024
	    LocalDateTime mockDate = LocalDateTime.of(2024, Month.JUNE, 18, 16, 30);

	    // LocalDateTime is a static class, hence we need to use mockStatic here
	    MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);

	    // I want to mock the now() function of the LocalDateTime class
	    // This means that later, when my program tries to run LocalDateTime.now(),
	    // it will always give the mock date instead of today's actual date
	    mocked.when(LocalDateTime::now).thenReturn(mockDate);

	    Song song = new Song("1", "Eric", "Test Song", 3.45);
	    SongCollection collection = new SongCollection();
	    collection.addSong(song);

	    // FIXED: Swapped parameters to follow standard (expected, actual) order
	    assertEquals("2024", collection.getYearCreated());

	    mocked.close();
	}
	@Test
	void testGetFullDateCreatedOfSongCollection() {
	    // 1. Setup the same pre-determined fixed date (18th June 2024)
	    LocalDateTime mockDate = LocalDateTime.of(2024, Month.JUNE, 18, 16, 30);

	    // 2. Open the static mock container
	    MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);
	    mocked.when(LocalDateTime::now).thenReturn(mockDate);

	    // 3. Run the test logic
	    Song song = new Song("1", "Eric", "Test Song", 3.45);
	    SongCollection collection = new SongCollection();
	    collection.addSong(song);

	    // 4. Assert that the date matches the "dd/MM/yyyy" format
	    assertEquals("18/06/2024", collection.getFullDateCreated());

	    // 5. Safely close the static mock
	    mocked.close();
	}
	
	@Test
	void testSameDateCreatedComparison() {
	    LocalDateTime mockDate = LocalDateTime.of(2025, Month.DECEMBER, 14, 16, 25);
	    LocalDateTime otherMockDate = LocalDateTime.of(2025, Month.DECEMBER, 14, 16, 25);

	    MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);

	    mocked.when(LocalDateTime::now).thenReturn(mockDate);
	    SongCollection firstCollection = new SongCollection();

	    mocked.when(LocalDateTime::now).thenReturn(otherMockDate);
	    SongCollection secondCollection = new SongCollection();

	    String result = "My collection was created at the same time!";
	    assertEquals(firstCollection.compareCollection(secondCollection), result);

	    mocked.close();
	}
	
	@Test
	void testBeforeDateCreatedComparison() {
	    LocalDateTime mockDate = LocalDateTime.of(2025, Month.DECEMBER, 12, 20, 30);
	    LocalDateTime otherMockDate = LocalDateTime.of(2025, Month.DECEMBER, 14, 16, 25);

	    MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);

	    mocked.when(LocalDateTime::now).thenReturn(mockDate);
	    SongCollection firstCollection = new SongCollection();

	    mocked.when(LocalDateTime::now).thenReturn(otherMockDate);
	    SongCollection secondCollection = new SongCollection();

	    String result = "My collection is older!";
	    assertEquals(firstCollection.compareCollection(secondCollection), result);

	    mocked.close();
	}
	
	@Test
	void testAfterDateCreatedComparison() { // Renamed to avoid duplicate method error
	    // swap the dates so the first one is now LATER (newer) than the second one
	    LocalDateTime mockDate = LocalDateTime.of(2025, Month.DECEMBER, 14, 16, 25);
	    LocalDateTime otherMockDate = LocalDateTime.of(2025, Month.DECEMBER, 12, 20, 30);

	    MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);

	    mocked.when(LocalDateTime::now).thenReturn(mockDate);
	    SongCollection firstCollection = new SongCollection();

	    mocked.when(LocalDateTime::now).thenReturn(otherMockDate);
	    SongCollection secondCollection = new SongCollection();

	    // Expect the "newer" string response
	    String result = "My collection is newer!"; 
	    assertEquals(firstCollection.compareCollection(secondCollection), result);

	    mocked.close();
	}
}