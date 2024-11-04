package models;
import java.util.List;
import java.util.OptionalDouble;


public class SearchResults {
    private String searchTerms;
    private List<YouTubeVideo> youTubeVideosList;
    private double averageFleschKincaidGradeLevel;
    private double averageFleschReadingEaseScore;

    public SearchResults(String searchTerms, List<YouTubeVideo> youTubeVideosList) {
        this.searchTerms = searchTerms;
        this.youTubeVideosList = youTubeVideosList;
        calculateAverageScores();
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

    private void calculateAverageScores() {
        // Using Streams to calculate the averages
        OptionalDouble avgGradeLevel = youTubeVideosList.stream()
            .mapToDouble(YouTubeVideo::getFleschKincaidGradeLevel)
            .average();
        
        OptionalDouble avgEaseScore = youTubeVideosList.stream()
            .mapToDouble(YouTubeVideo::getFleschReadingEaseScore)
            .average();

        averageFleschKincaidGradeLevel = avgGradeLevel.orElse(0.0);
        averageFleschReadingEaseScore = avgEaseScore.orElse(0.0);
    }
}