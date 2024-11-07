package models;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;


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
        List<Double> gradeLevels = new ArrayList<>();
        List<Double> easeScores = new ArrayList<>();

        for (YouTubeVideo video : youTubeVideosList) {
            gradeLevels.add(video.getFleschKincaidGradeLevel());
            easeScores.add(video.getFleschReadingEaseScore());
        }
        
        System.out.println("Grade Levels: " + gradeLevels);
        System.out.println("Ease Scores: " + easeScores);

        averageFleschKincaidGradeLevel = gradeLevels.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);

        averageFleschReadingEaseScore = easeScores.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        
        averageFleschKincaidGradeLevel = new BigDecimal(averageFleschKincaidGradeLevel).setScale(3, RoundingMode.HALF_UP).doubleValue();
        averageFleschReadingEaseScore = new BigDecimal(averageFleschReadingEaseScore).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}