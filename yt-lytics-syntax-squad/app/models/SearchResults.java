package models;
import java.util.List;


public class SearchResults {
    private String searchTerms;
    private List<YouTubeVideo> youTubeVideosList;

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
}