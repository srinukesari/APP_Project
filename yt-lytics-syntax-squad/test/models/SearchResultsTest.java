import models.SearchResults;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;



public class SearchResultsTest {

    private SearchResults searchResults;

    /* @author: sushmitha */
    @Test
    public void testSearchResultAverageScoreGetter() {
        searchResults = new SearchResults("sampleSearchKey",null);
        searchResults.setAverageFleschKincaidGradeLevel(20.0);
        searchResults.setAverageFleschReadingEaseScore(20.0);
        assertTrue(searchResults.getAverageFleschKincaidGradeLevel() == 20.0);
        assertTrue(searchResults.getAverageFleschReadingEaseScore() == 20.0);
    }
}
