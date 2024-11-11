package tests;

import models.SearchResults;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;


/**
 * Unit test for the SearchResults class.
 * This test verifies the getter methods for average Flesch-Kincaid Grade Level and Flesch Reading Ease Score.
 */
public class SearchResultsTest {

    private SearchResults searchResults;

    /**
     * Test case for getting the average Flesch-Kincaid Grade Level and Flesch Reading Ease Score.
     *
     * This test sets the average grade level and ease score and verifies that
     * the getter methods return the correct values.
     *
     * @author srinu.kesari
     */
    @Test
    public void testSearchResultAverageScoreGetter() {
        searchResults = new SearchResults("sampleSearchKey",null);
        searchResults.setAverageFleschKincaidGradeLevel(20.0);
        searchResults.setAverageFleschReadingEaseScore(20.0);
        assertTrue(searchResults.getAverageFleschKincaidGradeLevel() == 20.0);
        assertTrue(searchResults.getAverageFleschReadingEaseScore() == 20.0);
    }
}
