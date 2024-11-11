package tests;
import models.YouTubeVideo;
import models.MoreStats;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Mock;
import java.util.*;
import static org.junit.Assert.*;

/* @author: sahiti */
public class MoreStatsTest {

    @Mock
    private YouTubeVideo mockvideo1;

    @Mock
    private YouTubeVideo mockvideo2;

    private MoreStats moreStats;

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

    @Test
    public void testEmptyVideoList() {
        List<YouTubeVideo> emptyVideoList = new ArrayList<>();
        moreStats = new MoreStats("Empty", emptyVideoList);
        Map<String, Long> wordStatistics = moreStats.getWordStatistics();
        assertTrue(wordStatistics.isEmpty());
    }

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
