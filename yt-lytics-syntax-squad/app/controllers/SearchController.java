package controllers;
import models.*;

import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http;

import java.util.*;

import views.html.*;
import play.i18n.Messages;

import play.i18n.MessagesApi;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture; 

/* @author: Team */
public class SearchController  extends Controller{

    private final FormFactory formFactory;
    private final MessagesApi messagesApi;
    private final YouTubeSearch youTubeSearch;

    public List<SearchResults> displayResults = new ArrayList<>();
    public List<SearchResults> morestatsResults = new ArrayList<>();
    private double averageFleschKincaidGradeLevel;
    private double averageFleschReadingEaseScore;

    /**
     * Constructor to initialize the SearchController.
     *
     * @param formFactory The form factory to create forms.
     * @param messagesApi The messages API to handle message internationalization.
     * @param youTubeSearch The service used to search YouTube.
     *
     * @author srinu.kesari
     * @description Initializes the SearchController with the necessary dependencies.
     */
    @Inject
    public SearchController(FormFactory formFactory, MessagesApi messagesApi,YouTubeSearch youTubeSearch) {
        this.formFactory = formFactory;
        this.messagesApi = messagesApi;
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
    public CompletableFuture<Result> search(Http.Request request){
        return CompletableFuture.supplyAsync(() -> {
            try {
                Form<Search> searchForm = formFactory.form(Search.class).bindFromRequest(request);
                Messages messages = messagesApi.preferred(request);

                if (searchForm == null || searchForm.hasErrors()) {
                    return badRequest();
                }
                Search data = searchForm.get();
                if (data == null) return badRequest();

                String searchKey = data.getKey();
                if (searchKey != "") {
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
                        return badRequest("Exception occured from YoutubeApi");
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
                }
                Form<Search> newSearchForm = formFactory.form(Search.class);
                return ok(search.render(newSearchForm, displayResults, messages));
            }catch (Exception e){
                System.out.println("check here -=------>"+e);
                return badRequest("Exception occured");
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
    public CompletableFuture<Result> profile(Http.Request request){
        return CompletableFuture.supplyAsync(() -> {
            String channelName = request.getQueryString("channel");
            if (channelName == null) {
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

            return ok(profile.render(channelName,YTVideosList));
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
    public CompletableFuture<Result> tags(Http.Request request){
        return CompletableFuture.supplyAsync(() -> {
            String videoId = request.getQueryString("videoId");
            String hashTag = request.getQueryString("hashTag");

            if (videoId == null && hashTag == null) {
                return badRequest("videoId/ hashTag not provided");
            }
            List<YouTubeVideo> YTVideosList = new ArrayList<>();
            try {
                if(videoId != null){
                    YTVideosList = youTubeSearch.Search(videoId,"tags");
                    return ok(videotags.render(videoId,YTVideosList));
                } else {
                    YTVideosList = youTubeSearch.Search(hashTag,"hashTag");
                    return ok(tagsearch.render(hashTag,YTVideosList));
                }
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

                return ok(views.html.wordstats.render(wordStats));
            } else {
                return badRequest("No search results found for the given terms.");
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