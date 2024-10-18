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

    public static List<YouTubeVideo> Search(String search) throws IOException {
        YouTube youtubeService = new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY, (HttpRequestInitializer) null)
                .setApplicationName("YTLyticsSyntaxSquad")
                .build();

        YouTube.Search.List searchRequest = youtubeService.search()
                .list("snippet")
                .setQ(search)
                .setType("video")
                .setMaxResults(10L)
                .setKey(API_KEY);

        SearchListResponse response = searchRequest.execute();
        List<SearchResult> searchResults = response.getItems();

        List<YouTubeVideo> videosList = new ArrayList<>();

        for (SearchResult result : searchResults) {
            videosList.add(new YouTubeVideo(result.getSnippet().getTitle(),
                    result.getSnippet().getChannelTitle(),
                    result.getSnippet().getDescription()));
        }

        return videosList;
    }
}
