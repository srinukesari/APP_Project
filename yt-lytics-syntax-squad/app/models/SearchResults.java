package models;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The `SearchResults` class represents the results of a search query on YouTube,
 * including the list of YouTube videos, the average Flesch-Kincaid grade level,
 * and the average Flesch reading ease score of the video descriptions.
 *
 * This class is typically used to encapsulate the search results and statistics
 * for a particular search term.
 *
 * @author team
 */

public class SearchResults {
    private String searchTerms;
    private List<YouTubeVideo> youTubeVideosList;
    private double averageFleschKincaidGradeLevel;
    private double averageFleschReadingEaseScore;

    /**
     * Constructor to initialize `SearchResults` with search terms and a list of YouTube videos.
     *
     * @param searchTerms The search term(s) used to fetch the results.
     * @param youTubeVideosList The list of YouTube videos that matched the search query.
     */
    public SearchResults(String searchTerms, List<YouTubeVideo> youTubeVideosList) {
        this.searchTerms = searchTerms;
        this.youTubeVideosList = youTubeVideosList;
    }

    /**
     * Gets the search terms associated with the search results.
     *
     * @return The search terms.
     */
    @JsonProperty("searchTerms")
    public String getSearchTerms(){
        return searchTerms;
    }

    /**
     * Gets the list of YouTube videos related to the search results.
     *
     * @return The list of YouTube videos.
     */
    @JsonProperty("youTubeVideosList")
    public List<YouTubeVideo> getYouTubeVideosList() {
        return youTubeVideosList;
    }

    /**
     * Gets the average Flesch-Kincaid grade level for the video descriptions in the search results.
     *
     * @return The average Flesch-Kincaid grade level.
     */
    @JsonProperty("averageFleschKincaidGradeLevel")
    public double getAverageFleschKincaidGradeLevel() {
        return averageFleschKincaidGradeLevel;
    }

    /**
     * Gets the average Flesch reading ease score for the video descriptions in the search results.
     *
     * @return The average Flesch reading ease score.
     */
    @JsonProperty("averageFleschReadingEaseScore")
    public double getAverageFleschReadingEaseScore() {
        return averageFleschReadingEaseScore;
    }
    /**
     * Sets the average Flesch-Kincaid grade level for the search results.
     *
     * @param averageFleschKincaidGradeLevel The average grade level to set.
     */
    public void setAverageFleschKincaidGradeLevel(double averageFleschKincaidGradeLevel) {
        this.averageFleschKincaidGradeLevel = averageFleschKincaidGradeLevel;
    }

    /**
     * Sets the average Flesch reading ease score for the search results.
     *
     * @param averageFleschReadingEaseScore The average reading ease score to set.
     */
    public void setAverageFleschReadingEaseScore(double averageFleschReadingEaseScore) {
        this.averageFleschReadingEaseScore = averageFleschReadingEaseScore;
    }
}