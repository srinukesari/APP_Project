package models;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* @author: team */
public class SearchResults {
    private String searchTerms;
    private List<YouTubeVideo> youTubeVideosList;
    private double averageFleschKincaidGradeLevel;
    private double averageFleschReadingEaseScore;

    public SearchResults(String searchTerms, List<YouTubeVideo> youTubeVideosList) {
        this.searchTerms = searchTerms;
        this.youTubeVideosList = youTubeVideosList;
    }

    @JsonProperty("searchTerms")
    public String getSearchTerms(){
        return searchTerms;
    }

    @JsonProperty("youTubeVideosList")
    public List<YouTubeVideo> getYouTubeVideosList() {
        return youTubeVideosList;
    }

    @JsonProperty("averageFleschKincaidGradeLevel")
    public double getAverageFleschKincaidGradeLevel() {
        return averageFleschKincaidGradeLevel;
    }

    @JsonProperty("averageFleschReadingEaseScore")
    public double getAverageFleschReadingEaseScore() {
        return averageFleschReadingEaseScore;
    }
    public void setAverageFleschKincaidGradeLevel(double averageFleschKincaidGradeLevel) {
        this.averageFleschKincaidGradeLevel = averageFleschKincaidGradeLevel;
    }

    public void setAverageFleschReadingEaseScore(double averageFleschReadingEaseScore) {
        this.averageFleschReadingEaseScore = averageFleschReadingEaseScore;
    }
}