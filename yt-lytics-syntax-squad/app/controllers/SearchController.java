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

public class SearchController  extends Controller{

    private final FormFactory formFactory;
    private final MessagesApi messagesApi;
    private final YouTubeSearch youTubeSearch;

    public List<SearchResults> displayResults = new ArrayList<>();
    public List<SearchResults> morestatsResults = new ArrayList<>();
    private double averageFleschKincaidGradeLevel;
    private double averageFleschReadingEaseScore;

    @Inject
    public SearchController(FormFactory formFactory, MessagesApi messagesApi,YouTubeSearch youTubeSearch) {
        this.formFactory = formFactory;
        this.messagesApi = messagesApi;
        this.youTubeSearch = youTubeSearch;
    }

    public Result search(Http.Request request){
        Form<Search> searchForm = formFactory.form(Search.class).bindFromRequest(request);
        Messages messages = messagesApi.preferred(request);

        if(searchForm.hasErrors()){
            return badRequest();
        }
        Search data = searchForm.get();
        if(data == null) return badRequest();

        String searchKey = data.getKey();
        if(searchKey != null && !searchKey.isEmpty()) {
            // if(displayResults.size() > 0 && displayResults.get(0).getSearchTerms().trim().toLowerCase().equals(searchKey.trim().toLowerCase())){
            //     // top record is the search key no need to call api again
            // }else{
                List<YouTubeVideo> YTVideosList = new ArrayList<>();
                List<YouTubeVideo> MorestatsVideosList = new ArrayList<>();

                try {
                    YTVideosList = youTubeSearch.Search(searchKey,"home");
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
                }

                double averageFleschKincaidGradeLevel = calculateAverageFleschKincaidGradeLevel(MorestatsVideosList);
                double averageFleschReadingEaseScore = calculateAverageFleschReadingEaseScore(MorestatsVideosList);

                SearchResults sr = new SearchResults(searchKey, YTVideosList);
                SearchResults sr1 = new SearchResults(searchKey, MorestatsVideosList);
                sr.setAverageFleschKincaidGradeLevel(averageFleschKincaidGradeLevel);
                sr.setAverageFleschReadingEaseScore(averageFleschReadingEaseScore);
                displayResults.add(0, sr);
                morestatsResults.add(0, sr1);
            // }
        }
        Search emptySearch = new Search();
        emptySearch.setKey("");  
        return ok(search.render(searchForm.fill(emptySearch), displayResults, messages));

    }

    public Result profile(Http.Request request){
        String channelName = request.getQueryString("channel");
        if (channelName == null) {
            return badRequest("ChannelName not provided");
        }
        List<YouTubeVideo> YTVideosList = new ArrayList<>();
        try {
            YTVideosList = youTubeSearch.Search(channelName,"profile");
        } catch (Exception e) {
            System.out.println("check exception==== " + e);
        }

        return ok(profile.render(channelName,YTVideosList));
    }

    public Result tags(Http.Request request){
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
            System.out.println("check exception==== " + e);
        }

        return ok(videotags.render(videoId,YTVideosList));
    }


    public Result displayStats(String searchTerms) {
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
    }
    public double calculateAverageFleschKincaidGradeLevel(List<YouTubeVideo> videos) {
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
    }

    public double calculateAverageFleschReadingEaseScore(List<YouTubeVideo> videos) {
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
    }
    
}