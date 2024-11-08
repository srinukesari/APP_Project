package models;

import java.util.*;
import java.util.stream.Collectors;

public class MoreStats {
    private List<YouTubeVideo> youTubeVideosList;
    private String searchTerms;

    public MoreStats(String searchTerms, List<YouTubeVideo> youTubeVideosList) {
        this.searchTerms = searchTerms;
        this.youTubeVideosList = youTubeVideosList;
    }

    public Map<String, Long> getWordStatistics() {
        return youTubeVideosList.stream()
                .flatMap(video -> Arrays.stream((video.getTitle() + " " + video.getDescription()).split("\\W+")))
                .filter(word -> !word.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
