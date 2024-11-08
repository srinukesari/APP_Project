package models;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public String getSearchTerms(){
        return searchTerms;
    }

    public List<YouTubeVideo> getYouTubeVideosList() {
        return youTubeVideosList;
    }
    public double getAverageFleschKincaidGradeLevel() {
        return averageFleschKincaidGradeLevel;
    }

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