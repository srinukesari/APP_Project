package tests;
import models.YouTubeVideo;
import models.MoreStats;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Mock;
import java.util.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the MoreStats class.
 *
 * This test class verifies the behavior of the `MoreStats` class, which analyzes word statistics
 * for a list of YouTube video titles and descriptions. The tests ensure the correctness of word
 * frequency calculations and handle edge cases such as an empty video list and single-word content.
 *
 * @author sahiti
 */
public class MoreStatsTest {

    @Mock
    private YouTubeVideo mockvideo1;

    @Mock
    private YouTubeVideo mockvideo2;

    private MoreStats moreStats;

    /**
     * Sets up the test environment before each test.
     *
     * Initializes mock YouTube videos and the `MoreStats` object. Mocks provide test data for
     * video titles and descriptions to validate word frequency calculations.
     */
    @Before
    public void setup() {
        mockvideo1 = Mockito.mock(YouTubeVideo.class);
        mockvideo2 = Mockito.mock(YouTubeVideo.class);
        Mockito.when(mockvideo1.getTitle()).thenReturn("Java Programming Tutorial");
        Mockito.when(mockvideo1.getDescription()).thenReturn("Learn Java programming from scratch!");
        Mockito.when(mockvideo2.getTitle()).thenReturn("Advanced Java Concepts");
        Mockito.when(mockvideo2.getDescription()).thenReturn("Dive deep into advanced Java programming topics.");
        List<YouTubeVideo> youTubeVideosList = new ArrayList<>();
        youTubeVideosList.add(mockvideo1);
        youTubeVideosList.add(mockvideo2);
        moreStats = new MoreStats("Java", youTubeVideosList);
    }

    /**
     * Tests the MoreStats#getWordStatistics() method for a list of videos with valid titles and descriptions.
     *
     * Verifies that the word statistics are calculated correctly by comparing the actual output
     * with the expected word-frequency mapping.
     */
    @Test
    public void testGetWordStatistics() {
        Map<String, Long> wordStatistics = moreStats.getWordStatistics();
        Map<String, Long> expectedStatistics = new LinkedHashMap<>();
        expectedStatistics.put("java", 4L); 
        expectedStatistics.put("programming", 3L); 
        expectedStatistics.put("tutorial", 1L); 
        expectedStatistics.put("learn", 1L); 
        expectedStatistics.put("from", 1L); 
        expectedStatistics.put("scratch", 1L); 
        expectedStatistics.put("advanced", 2L); 
        expectedStatistics.put("concepts", 1L); 
        expectedStatistics.put("dive", 1L);
        expectedStatistics.put("deep", 1L); 
        expectedStatistics.put("into", 1L); 
        expectedStatistics.put("topics", 1L); 
        assertEquals(expectedStatistics, wordStatistics);
    }

    /**
     * Tests the MoreStats#getWordStatistics() method with an empty video list.
     *
     * Verifies that the word statistics map is empty when there are no videos to analyze.
     */
    @Test
    public void testEmptyVideoList() {
        List<YouTubeVideo> emptyVideoList = new ArrayList<>();
        moreStats = new MoreStats("Empty", emptyVideoList);
        Map<String, Long> wordStatistics = moreStats.getWordStatistics();
        assertTrue(wordStatistics.isEmpty());
    }

    /**
     * Tests the MoreStats#getWordStatistics() method with a single video containing repetitive words.
     *
     * Verifies that the word statistics accurately capture word frequency, even for repetitive content.
     */
    @Test
    public void testSingleWordVideoList() {
        List<YouTubeVideo> singleWordVideos = new ArrayList<>();
        YouTubeVideo video = new YouTubeVideo("Id1", "hello", "TestChannel", "A simple hello video", "thumbnailUrl", null);
        singleWordVideos.add(video);
        moreStats = new MoreStats("Test", singleWordVideos);
        Map<String, Long> wordStatistics = moreStats.getWordStatistics();
        System.out.println("Wordstats for func1: " + wordStatistics);
        assertEquals(4, wordStatistics.size());
        assertEquals(2L, (long) wordStatistics.get("hello"));
    }
}
