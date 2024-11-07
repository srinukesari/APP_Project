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
import java.text.DecimalFormat;
import com.google.gson.Gson;

public class SearchController  extends Controller{

    private final FormFactory formFactory;
    private final MessagesApi messagesApi;
    private final YouTubeSearch youTubeSearch;

    public List<SearchResults> displayResults = new ArrayList<>();
    public List<SearchResults> morestatsResults = new ArrayList<>();

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
        String searchKey = data.getKey();
        if(searchKey != null && !searchKey.isEmpty()) {
            // if(displayResults.size() > 0 && displayResults.get(0).getSearchTerms().trim().toLowerCase().equals(searchKey.trim().toLowerCase())){
            //     // top record is the search key no need to call api again
            // }else{
                List<YouTubeVideo> YTVideosList = new ArrayList<>();
                List<YouTubeVideo> MorestatsVideosList = new ArrayList<>();

                try {
                    YTVideosList = youTubeSearch.Search(searchKey,"home");
                    MorestatsVideosList = youTubeSearch.Search(searchKey,"home");
                    if (YTVideosList.size() > 10) {
                        YTVideosList = YTVideosList.subList(0, 10); 
                    }
                } catch (Exception e) {
                    System.out.println("check exception==== " + e);
                }
                System.out.println("comes here to check111 " + searchForm + data + searchKey);
                SearchResults sr = new SearchResults(searchKey, YTVideosList);
                SearchResults sr1 = new SearchResults(searchKey, MorestatsVideosList);
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
                System.out.println("check the size "+YTVideosList);
                return ok(videotags.render(videoId,YTVideosList));
            } else {
                YTVideosList = youTubeSearch.Search(hashTag,"hashTag");
                System.out.println("check the size "+YTVideosList);

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
    
}