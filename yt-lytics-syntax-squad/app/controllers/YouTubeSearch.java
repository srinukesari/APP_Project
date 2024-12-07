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

/**
 * YouTubeSearch is a utility class that interacts with the YouTube Data API v3.
 * It provides methods for searching videos by keyword, channel, hashtag, and video ID.
 * The class also fetches video details like descriptions and tags.
 *
 * @author Team
 */
public class YouTubeSearch {
    private static final String API_KEY =
    "AIzaSyDns0F0kLwtiRhq522gR1IS3eFhEUlxm_E";
//     "AIzaSyAtag88ktltd4rYHadr5fT4eldZ0E-TJ6I";
//    "AIzaSyATDszuEMCnKfoxdjnT9rhfvpL3ONbsYtE";
//    "AIzaSyD52jNisyPmmiFWWQ_YckDUx4VAxUAyCK4";
//    "AIzaSyD0a1-a6o2zk6koHCCA-yJ827fRfPpDP5U";
    //"AIzaSyCUFy3WvnJYPDmrv6tA80xGw3-uzjo36bk"; // srinu's api key

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private YouTube youtubeService;

    /**
     * Initializes the YouTubeSearch with the default YouTube API service.
     *
     * @author Team
     * @description Constructor that initializes the YouTubeSearch with the YouTube API service.
     */
    public YouTubeSearch(){
        this.youtubeService =  new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY, (HttpRequestInitializer) null)
                .setApplicationName("YTLyticsSyntaxSquad")
                .build();
    }

    /**
     * Initializes the YouTubeSearch with a custom YouTube API service.
     *
     * @param youtubeService The YouTube service to use.
     *
     * @author Team
     * @description Constructor to initialize the YouTubeSearch with a custom YouTube API service.
     */
    public YouTubeSearch(YouTube youtubeService){
        this.youtubeService = youtubeService;
    }

    /**
     * Creates a YouTube search request to retrieve videos from a specific channel based on the channel name.
     *
     * @param youtubeService The YouTube service to use for the search.
     * @param channelName The name of the channel to search for.
     * @return The search request for retrieving videos from the channel.
     * @throws IOException If an error occurs while making the API request.
     *
     * @author sushmitha
     * @description This method constructs and executes a search request to find videos by channel name.
     */
    public static YouTube.Search.List getSearchRequestforProfile(YouTube youtubeService,String channelName)
            throws IOException{
        System.out.println("check channel name ----- "+channelName);
        YouTube.Search.List searchChannelRequest = youtubeService.search()
                .list("snippet")
                .setQ(channelName)
                .setType("video")
                .setMaxResults(1L)
                .setKey(API_KEY);

        SearchListResponse channelResponse = searchChannelRequest.execute();
        List<SearchResult> searchResultList = channelResponse.getItems();

        if (searchResultList.isEmpty()) {
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

    /**
     * Creates a YouTube search request to retrieve videos based on a search key for the home page.
     *
     * @param youtubeService The YouTube service to use for the search.
     * @param searchKey The search key used to find videos.
     * @return The search request for retrieving videos based on the search key.
     * @throws IOException If an error occurs while making the API request.
     *
     * @author aniket
     * @description This method constructs and executes a search request for videos based on a keyword search.
     */
    public static YouTube.Search.List getSearchRequestforHome(YouTube youtubeService,String searchKey)
            throws IOException{
        return youtubeService.search()
                .list("snippet")
                .setQ(searchKey)
                .setType("video")
                .setMaxResults(50L)
                .setKey(API_KEY);
    }

    /**
     * Creates a YouTube video request to retrieve details for a specific video based on its ID.
     *
     * @param youtubeService The YouTube service to use for the request.
     * @param videoId The ID of the video for which details are to be retrieved.
     * @return The video request for retrieving details of the video.
     * @throws IOException If an error occurs while making the API request.
     *
     * @author srinu.kesari
     * @description This method constructs and executes a request to get video details by video ID.
     */
    public static YouTube.Videos.List getVideoRequestforTags(YouTube youtubeService,String videoId)
            throws IOException{
        return youtubeService.videos()
                .list("snippet")
                .setId(videoId)
                .setMaxResults(1L)
                .setKey(API_KEY);
    }

    /**
     * Creates a YouTube search request to retrieve videos based on a hashtag.
     *
     * @param youtubeService The YouTube service to use for the search.
     * @param hashTag The hashtag used to search for videos.
     * @return The search request for retrieving videos based on the hashtag.
     * @throws IOException If an error occurs while making the API request.
     *
     * @author srinu.kesari
     * @description This method constructs and executes a search request to find videos based on a hashtag.
     */
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

    /**
     * Searches for YouTube videos based on a search key or video ID, depending on the requested page type.
     *
     * @param search The search key or video ID to search for.
     * @param page The page type (home, profile, tags, or hashTag).
     * @return A list of YouTube video objects matching the search criteria.
     * @throws IOException If an error occurs while making the API request.
     *
     * @author srinu.kesari
     * @description This method searches for YouTube videos based on the search key or video ID
     *              and page type, returning a list of matching videos.
     */
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

//        videosList.add(
//                new YouTubeVideo(
//                        "sample Id",
//                        "sample Title",
//                        "sample Channel",
//                        "sample Description",
//                        null,
//                        null
//                )
//        );
        // execute the request and get the details of the video
        if(searchRequest == null && videoRequest == null) return videosList;
        else if(searchRequest != null) {
            SearchListResponse searchResponse = searchRequest.execute();
            searchResults = searchResponse.getItems();
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

    /**
     * Fetches the full descriptions and details of videos by their IDs.
     *
     * @param videoIds The list of video IDs to retrieve full descriptions for.
     * @return A list of YouTubeVideo objects with detailed information.
     * @throws IOException If an error occurs while making the API request.
     *
     * @author sahiti
     * @description This method fetches the full descriptions of a list of videos based on their IDs.
     */
    public List<YouTubeVideo> fetchFullDescriptions(List<String> videoIds) throws IOException {
        List<YouTubeVideo> videosList = new ArrayList<>();
        YouTube.Videos.List videoRequest = youtubeService.videos()
                .list("snippet")
                .setKey(API_KEY);

//        videosList.add(
//                new YouTubeVideo(
//                        "sample Id",
//                        "sample Title",
//                        "sample Channel",
//                        "sample Description",
//                        null,
//                        null
//                )
//        );
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
