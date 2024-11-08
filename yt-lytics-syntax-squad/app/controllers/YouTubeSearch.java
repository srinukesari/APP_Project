package controllers;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.util.*;
import models.*;

/* @author: Team */
public class YouTubeSearch {
    private static final String API_KEY = "AIzaSyD0a1-a6o2zk6koHCCA-yJ827fRfPpDP5U";
            //"AIzaSyATDszuEMCnKfoxdjnT9rhfvpL3ONbsYtE";
            //"AIzaSyD0a1-a6o2zk6koHCCA-yJ827fRfPpDP5U";
    //"AIzaSyCUFy3WvnJYPDmrv6tA80xGw3-uzjo36bk"; // srinu's api key

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private YouTube youtubeService;

    public YouTubeSearch(){
        this.youtubeService =  new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY, (HttpRequestInitializer) null)
                .setApplicationName("YTLyticsSyntaxSquad")
                .build();
    }

    public YouTubeSearch(YouTube youtubeService){
        this.youtubeService = youtubeService;
    }

    /* @author: sushmitha */
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
                .setMaxResults(50L)
                .setKey(API_KEY);

        return searchVideosRequest;
    }

    /* @author: aniket */
    public static YouTube.Search.List getSearchRequestforHome(YouTube youtubeService,String searchKey)
            throws IOException{
        return youtubeService.search()
                .list("snippet")
                .setQ(searchKey)
                .setType("video")
                .setMaxResults(50L)
                .setKey(API_KEY);
    }

    /* @author: srinu.kesari */
    public static YouTube.Videos.List getVideoRequestforTags(YouTube youtubeService,String videoId)
            throws IOException{
        return youtubeService.videos()
                .list("snippet")
                .setId(videoId)
                .setMaxResults(1L)
                .setKey(API_KEY);
    }

    /* @author: srinu.kesari */
    public static YouTube.Search.List getSearchRequestforTags(YouTube youtubeService,String hashTag)
            throws IOException{
        return youtubeService.search()
                .list("snippet")
                .setQ(hashTag)
                .setType("video")
                .setOrder("date")
                .setMaxResults(10L)
                .setKey(API_KEY);
    }

    /* @author: srinu.kesari */
    public List<YouTubeVideo> Search(String search, String page) throws IOException {
        List<YouTubeVideo> videosList = new ArrayList<>();
        List<SearchResult> searchResults = new ArrayList<>();
        List<Video> videoDetails = new ArrayList<>();

        YouTube.Search.List searchRequest = null;
        YouTube.Videos.List videoRequest = null;

        //get the search or video request to execute based on page
        if(page == "home"){
            searchRequest = getSearchRequestforHome(youtubeService,search);
        }else if(page == "profile"){
            searchRequest = getSearchRequestforProfile(youtubeService,search);
        }else if(page == "tags"){
            videoRequest = getVideoRequestforTags(youtubeService,search);
        }else if(page == "hashTag"){
            searchRequest = getSearchRequestforTags(youtubeService,search);
        }

        // execute the request and get the details of the video
        if(searchRequest == null && videoRequest == null) return videosList;
        else if(searchRequest != null) {
            SearchListResponse searchResponse = searchRequest.execute();
            searchResults = searchResponse.getItems();
            System.out.println("result is here "+searchResults.size());
        } else if (videoRequest != null) {
            VideoListResponse videoResponse = videoRequest.execute();
            videoDetails = videoResponse.getItems();
        }


        // based on the page show the videos to the end user
        if(page == "tags") {
            for (Video video : videoDetails) {
                // String fullDescription = video.getSnippet().getDescription();
                List<String> tags = video.getSnippet().getTags();
                String thumbnailUrl = video.getSnippet().getThumbnails().getDefault().getUrl();
                videosList.add(
                        new YouTubeVideo(
                                video.getId(),
                                video.getSnippet().getTitle(),
                                video.getSnippet().getChannelTitle(),
                                video.getSnippet().getDescription(),
                                thumbnailUrl,
                                tags
                        )
                );
            }
        }else{
            for (SearchResult result : searchResults) {
                String thumbnailUrl = result.getSnippet().getThumbnails().getDefault().getUrl();
                videosList.add(
                        new YouTubeVideo(
                                result.getId().getVideoId(),
                                result.getSnippet().getTitle(),
                                result.getSnippet().getChannelTitle(),
                                result.getSnippet().getDescription(),
                                thumbnailUrl,
                                null
                        )
                );
            }
        }
        return videosList;
    }

    /* @author: sahithi */
    public List<YouTubeVideo> fetchFullDescriptions(List<String> videoIds) throws IOException {
        List<YouTubeVideo> videosList = new ArrayList<>();
        YouTube.Videos.List videoRequest = youtubeService.videos()
                .list("snippet")  
                .setKey(API_KEY);

        for (int i = 0; i < videoIds.size(); i += 50) {
            List<String> batch = videoIds.subList(i, Math.min(i + 50, videoIds.size()));
            videoRequest.setId(String.join(",", batch)); 
            VideoListResponse videoResponse = videoRequest.execute();
            
            for (Video video : videoResponse.getItems()) {
                YouTubeVideo youtubeVideo = new YouTubeVideo(
                    video.getId(),
                    video.getSnippet().getTitle(),
                    video.getSnippet().getChannelTitle(),
                    video.getSnippet().getDescription(),  
                    video.getSnippet().getThumbnails().getDefault().getUrl(),
                    video.getSnippet().getTags()
                );
                videosList.add(youtubeVideo);
            }
        }
        return videosList;
    }
}
