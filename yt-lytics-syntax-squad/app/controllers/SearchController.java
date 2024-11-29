package controllers;
import models.*;

import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http;

import java.util.*;
import play.libs.Json;

import views.html.*;
import play.i18n.Messages;

import play.i18n.MessagesApi;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/* @author: Team */
public class SearchController  extends Controller{

    private final YouTubeSearch youTubeSearch;

    public List<SearchResults> displayResults = new ArrayList<>();
    public List<SearchResults> morestatsResults = new ArrayList<>();
    private double averageFleschKincaidGradeLevel;
    private double averageFleschReadingEaseScore;

    /**
     * Constructor to initialize the SearchController.
     *
     * @param youTubeSearch The service used to search YouTube.
     *
     * @author srinu.kesari
     * @description Initializes the SearchController with the necessary dependencies.
     */
    @Inject
    public SearchController(YouTubeSearch youTubeSearch) {
        this.youTubeSearch = youTubeSearch;
    }

    /**
     * Handles the search request and fetches YouTube videos based on the search key.
     *
     * @param request The HTTP request containing the search form data.
     * @return The result of the search, returning the view with the search results or an error page.
     *
     * @author aniket
     * @description This method handles the search request by binding form data, performing the YouTube
     *              search, and returning the appropriate results or error.
     */
    public CompletableFuture<Result> search(String searchKey) {
        if (searchKey == null || searchKey.trim().isEmpty()) {
            return CompletableFuture.completedFuture(badRequest("Search key cannot be empty"));
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<YouTubeVideo> YTVideosList = new ArrayList<>();
                List<YouTubeVideo> MorestatsVideosList = new ArrayList<>();
                try {
                    YTVideosList = youTubeSearch.Search(searchKey, "home");
                    List<String> videoIds = new ArrayList<>();
                    for (YouTubeVideo video : YTVideosList) {
                        videoIds.add(video.getVideoId());
                    }
                    YTVideosList = youTubeSearch.fetchFullDescriptions(videoIds);
                    MorestatsVideosList.addAll(YTVideosList);
                    if (YTVideosList.size() > 10) {
                        YTVideosList = YTVideosList.subList(0, 10);
                    }
                } catch (Exception e) {
                    System.out.println("check exception==== " + e);
                    return badRequest(Json.toJson("Exception occured from YoutubeApi"));
                }
                CompletableFuture<Double> gradeLevelFuture = calculateAverageFleschKincaidGradeLevel(MorestatsVideosList);
                CompletableFuture<Double> easeScoreFuture = calculateAverageFleschReadingEaseScore(MorestatsVideosList);

                // Wait for both calculations to complete
                CompletableFuture.allOf(gradeLevelFuture, easeScoreFuture).join();

                SearchResults sr = new SearchResults(searchKey, YTVideosList);
                SearchResults sr1 = new SearchResults(searchKey, MorestatsVideosList);
                sr.setAverageFleschKincaidGradeLevel(averageFleschKincaidGradeLevel);
                sr.setAverageFleschReadingEaseScore(averageFleschReadingEaseScore);
                displayResults.add(0, sr);
                morestatsResults.add(0, sr1);

                System.out.println("Morestats" + morestatsResults);

                JsonNode jsonNode = Json.toJson(sr);
//                System.out.println("hello.  ----"+jsonNode.toString());
                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.put("path","search");
                return ok(Json.toJson(objectNode));
            } catch (Exception e) {
                System.out.println("check here -=------>" + e);
                return badRequest(Json.toJson("Exception occured"));
            }
        });
    }

    /**
     * Searches YouTube for a channel's videos by channel name.
     *
     * @param request The HTTP request containing the channel name.
     * @return A page showing the videos of the given channel or an error if the channel is not found.
     *
     * @author sushmitha
     * @description This method handles the request to search for YouTube videos based on a channel's name.
     */
    public CompletableFuture<Result> profile(String channelName){
        return CompletableFuture.supplyAsync(() -> {
            if (channelName == null || channelName.isEmpty()) {
                return badRequest("ChannelName not provided");
            }
            List<YouTubeVideo> YTVideosList = new ArrayList<>();
            try {
                YTVideosList = youTubeSearch.Search(channelName,"profile");
                if (YTVideosList.size() > 10) {
                    YTVideosList = YTVideosList.subList(0, 10); 
                }
            } catch (Exception e) {
                return badRequest("Invalid API Key");
            }
            JsonNode result = Json.toJson(YTVideosList);
            ObjectNode jsonNode = Json.newObject();;
            jsonNode.put("path","profile");
            jsonNode.put("channel",channelName);
            jsonNode.put("youTubeVideosList",result);
            System.out.println("hello.  ----"+jsonNode.toString());
            return ok(jsonNode);
        });
    }

    /**
     * Searches for videos related to a specific video ID or hashtag.
     *
     * @param request The HTTP request containing either a videoId or a hashTag.
     * @return A page displaying videos related to the provided video ID or hashtag.
     *
     * @author srinu.kesari
     * @description This method searches for YouTube videos either by videoId or hashTag and renders the results.
     */
    public CompletableFuture<Result> tags(String type, String id){
        return CompletableFuture.supplyAsync(() -> {
            if (type == null || id == null || id.isEmpty()) {
                return badRequest("videoId/ hashTag not provided");
            }
            List<YouTubeVideo> YTVideosList = new ArrayList<>();
            System.out.println("inside tag call srinu---> "+ type+" ---- "+id+" "+type.equals("tags"));
            try {
                if(type.equals("tags")){
                    YTVideosList = youTubeSearch.Search(id,"tags");
                    JsonNode result = Json.toJson(YTVideosList);
                    System.out.println("result in tags.  ----"+result.toString());
                    ObjectNode jsonNode = Json.newObject();;
                    jsonNode.put("path","tags");
                    jsonNode.put("youTubeVideosList",result);
                    System.out.println("hello.  ----"+jsonNode.toString());
                    return ok(jsonNode);
                } else if(type.equals("hashTag")){
                    YTVideosList = youTubeSearch.Search(id,"hashTag");
                    JsonNode result = Json.toJson(YTVideosList);
                    System.out.println("result in hashtag.  ----"+result.toString());
                    ObjectNode jsonNode = Json.newObject();;
                    jsonNode.put("path","hashTag");
                    jsonNode.put("youTubeVideosList",result);
                    System.out.println("hello.  ----"+jsonNode.toString());
                    return ok(jsonNode);
                }
                ObjectNode jsonNode = Json.newObject();;
                jsonNode.put("path","tags");
                return ok(jsonNode);
            } catch (Exception e) {
                return badRequest("Invalid API Key");
            }
        });
    }

    /**
     * Displays statistics related to the video content for the given search terms.
     *
     * @param searchTerms The search terms used to retrieve the videos.
     * @return A page displaying the word statistics for the search results.
     *
     * @author sahithi
     * @description This method retrieves and displays word statistics for YouTube videos based on search terms.
     */
    public CompletableFuture<Result> displayStats(String searchTerms) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<SearchResults> searchResultsOpt = morestatsResults.stream()
                .filter(sr -> sr.getSearchTerms().equals(searchTerms))
                .findFirst();

            if (searchResultsOpt.isPresent()) {
                List<YouTubeVideo> videos = searchResultsOpt.get().getYouTubeVideosList();
                MoreStats stats = new MoreStats(searchTerms, videos);
                Map<String, Long> wordStats = stats.getWordStatistics();
                JsonNode jsonNode = Json.toJson(wordStats);
//                System.out.println("hello.  ----"+jsonNode.toString());
                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.put("path","stats");
                return ok(Json.toJson(objectNode));

                // return ok(views.html.wordstats.render(wordStats));
            } else {
                return badRequest(Json.toJson(Map.of("error", "No search results found in displaystats function.")));
            }
        });
    }

    /**
     * Calculates the average Flesch-Kincaid grade level for a list of YouTube videos.
     *
     * @param videos A list of YouTubeVideo objects to calculate the average grade level.
     * @return The average Flesch-Kincaid grade level.
     *
     * @author sahithi
     * @description This method calculates the average Flesch-Kincaid grade level from a list of YouTube videos.
     */
    public CompletableFuture<Double> calculateAverageFleschKincaidGradeLevel(List<YouTubeVideo> videos) {
        return CompletableFuture.supplyAsync(() -> {
            List<Double> gradeLevels = new ArrayList<>();
            for (YouTubeVideo video : videos) {
                gradeLevels.add(video.getFleschKincaidGradeLevel());
            }
            averageFleschKincaidGradeLevel = gradeLevels.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            averageFleschKincaidGradeLevel = new BigDecimal(averageFleschKincaidGradeLevel).setScale(3, RoundingMode.HALF_UP).doubleValue();
            return averageFleschKincaidGradeLevel;
        });
    }

    /**
     * Calculates the average Flesch reading ease score for a list of YouTube videos.
     *
     * @param videos A list of YouTubeVideo objects to calculate the average reading ease score.
     * @return The average Flesch reading ease score.
     *
     * @author sahithi
     * @description This method calculates the average Flesch reading ease score from a list of YouTube videos.
     */
    public CompletableFuture<Double> calculateAverageFleschReadingEaseScore(List<YouTubeVideo> videos) {
        return CompletableFuture.supplyAsync(() -> {
            List<Double> easeScores = new ArrayList<>();
            for (YouTubeVideo video : videos) {
                easeScores.add(video.getFleschReadingEaseScore());
            }
            averageFleschReadingEaseScore = easeScores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            averageFleschReadingEaseScore = new BigDecimal(averageFleschReadingEaseScore).setScale(3, RoundingMode.HALF_UP).doubleValue();
            return averageFleschReadingEaseScore;
        });
    }
}