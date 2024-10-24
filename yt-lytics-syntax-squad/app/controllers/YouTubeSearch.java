package controllers;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.*;
import models.*;

public class YouTubeSearch {
    private static final String API_KEY = "AIzaSyCUFy3WvnJYPDmrv6tA80xGw3-uzjo36bk"; // srinu's api key
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static YouTube.Search.List getSearchRequestforProfile(YouTube youtubeService,String channelName)
            throws IOException{
        YouTube.Search.List searchChannelRequest = youtubeService.search()
                .list("snippet")
                .setQ(channelName)
                .setType("video")
                .setMaxResults(1L)
                .setKey(API_KEY);

        SearchListResponse channelResponse = searchChannelRequest.execute();
        List<SearchResult> searchResultList = channelResponse.getItems();

        if (searchResultList.isEmpty()) {
            System.out.println("No channels found for: " + channelName);
            return null;
        }

        String channelId = searchResultList.get(0).getSnippet().getChannelId();
        System.out.println("Found channel ID: " + channelId);

        YouTube.Search.List searchVideosRequest = youtubeService.search()
                .list("snippet")
                .setChannelId(channelId)
                .setType("video")
                .setOrder("date")
                .setMaxResults(10L)
                .setKey(API_KEY);

        return searchVideosRequest;
    }

    public static YouTube.Search.List getSearchRequestforHome(YouTube youtubeService,String searchKey)
            throws IOException{
        return youtubeService.search()
                .list("snippet")
                .setQ(searchKey)
                .setType("video")
                .setMaxResults(10L)
                .setKey(API_KEY);
    }


    public static List<YouTubeVideo> Search(String search, String page) throws IOException {
        List<YouTubeVideo> videosList = new ArrayList<>();

        YouTube youtubeService = new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY, (HttpRequestInitializer) null)
                .setApplicationName("YTLyticsSyntaxSquad")
                .build();

        YouTube.Search.List searchRequest = null;
        if(page == "home"){
            searchRequest = getSearchRequestforHome(youtubeService,search);

        }else if(page == "profile"){
            searchRequest = getSearchRequestforProfile(youtubeService,search);
        }

        if(searchRequest == null) return videosList;

        SearchListResponse response = searchRequest.execute();
        List<SearchResult> searchResults = response.getItems();

        for (SearchResult result : searchResults) {
            videosList.add(new YouTubeVideo(result.getSnippet().getTitle(),
                    result.getSnippet().getChannelTitle(),
                    result.getSnippet().getDescription()));
        }

        return videosList;
    }
}
