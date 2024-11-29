package models;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The `MoreStats` class is responsible for generating word statistics from a list of YouTube videos.
 * It processes the title and description of each video, tokenizes the text, and counts the occurrences
 * of each word, providing a way to analyze the frequency of words in the given video data.
 *
 * @author sahiti
 */
public class MoreStats {
    private List<YouTubeVideo> youTubeVideosList;
    private String searchTerms;

    /**
     * Constructor for `MoreStats`.
     * Initializes the `MoreStats` object with the provided search terms and a list of YouTube videos.
     *
     * @param searchTerms The search terms used to retrieve the YouTube videos.
     * @param youTubeVideosList The list of YouTube videos to analyze.
     */

    public MoreStats(String searchTerms, List<YouTubeVideo> youTubeVideosList) {
        this.searchTerms = searchTerms;
        this.youTubeVideosList = youTubeVideosList;
    }
    /**
     * Analyzes the list of YouTube videos and computes word statistics, including the frequency of each word
     * in the combined title and description of all videos.
     * Words are tokenized, converted to lowercase, and counted. The result is a map where the keys are words,
     * and the values are the number of occurrences of each word. The map is sorted by frequency in descending order.
     *
     * @return A `Map` containing the word statistics, sorted by frequency in descending order.
     * The map is returned as a `LinkedHashMap` to maintain the order of the elements.
     */

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
