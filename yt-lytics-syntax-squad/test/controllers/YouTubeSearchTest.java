import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import models.YouTubeVideo;
import controllers.YouTubeSearch;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class YouTubeSearchTest {

    private YouTube youtubeService;
    private YouTubeSearch youtubeSearch;
    private YouTubeSearch youtubeSearchDefault;

    @Before
    public void setUp() {
        youtubeService = mock(YouTube.class, RETURNS_DEEP_STUBS);
        youtubeSearch = new YouTubeSearch(youtubeService);
        youtubeSearchDefault = new YouTubeSearch();
    }

    /* @author: sushmitha */
    @Test
    public void testGetSearchRequestforProfile_FoundChannel() throws IOException {
        YouTube.Search.List searchChannelRequest = mock(YouTube.Search.List.class);
        SearchListResponse channelResponse = mock(SearchListResponse.class);

        Mockito.when(youtubeService.search().list("snippet")).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setQ(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setChannelId(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setType(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setOrder(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setMaxResults(anyLong())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setKey(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.execute()).thenReturn(channelResponse);

        SearchResult mockResult = mock(SearchResult.class, RETURNS_DEEP_STUBS);
        Mockito.when(channelResponse.getItems()).thenReturn(Collections.singletonList(mockResult));
        Mockito.when(mockResult.getSnippet().getChannelId()).thenReturn("testChannelId");

        List<YouTubeVideo> result = youtubeSearch.Search("testSearchKey", "profile");

//        verify(searchChannelRequest).setChannelId("testChannelId");

        assertNotNull(result);
    }

    /* @author: sushmitha */
    @Test
    public void testGetSearchRequestforProfile_NoChannelsFound() throws IOException {
        YouTube.Search.List searchChannelRequest = mock(YouTube.Search.List.class);
        SearchListResponse channelResponse = mock(SearchListResponse.class);

        Mockito.when(youtubeService.search().list("snippet")).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setQ(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setChannelId(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setType(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setOrder(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setMaxResults(anyLong())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.setKey(anyString())).thenReturn(searchChannelRequest);
        Mockito.when(searchChannelRequest.execute()).thenReturn(channelResponse);
        Mockito.when(channelResponse.getItems()).thenReturn(Collections.emptyList());

        YouTube.Search.List result = YouTubeSearch.getSearchRequestforProfile(youtubeService, "unknownChannel");
        assertNull(result);
    }

    /* @author: aniket */
    @Test
    public void testGetSearchRequestforHome() throws IOException {
        YouTube.Search.List searchRequest = YouTubeSearch.getSearchRequestforHome(youtubeService, "sampleSearch");
        assertNotNull(searchRequest);
    }

    /* @author: srinu.kesari */
    @Test
    public void testGetSearchRequestforTags() throws IOException {
        YouTube.Search.List searchRequest = YouTubeSearch.getSearchRequestforTags(youtubeService, "sampleSearch");
        assertNotNull(searchRequest);
    }

    /* @author: srinu.kesari */
    @Test
    public void testGetVideoRequestforTags() throws IOException {
        YouTube.Videos.List searchRequest = YouTubeSearch.getVideoRequestforTags(youtubeService, "sampleSearch");
        assertNotNull(searchRequest);
    }

    /* @author: aniket */
    @Test
    public void testSearch_HomePage_EmptyResult() throws IOException {

        YouTube youtubeService = mock(YouTube.class);
        YouTube.Search search = mock(YouTube.Search.class);

        YouTube.Search.List searchRequest = mock(YouTube.Search.List.class);
        SearchListResponse searchResponse = mock(SearchListResponse.class);



        Mockito.when(youtubeService.search()).thenReturn(search);
        Mockito.when(search.list("snippet")).thenReturn(searchRequest);

        Mockito.when(searchRequest.setQ(anyString())).thenReturn(searchRequest);
        Mockito.when(searchRequest.setType(anyString())).thenReturn(searchRequest);
        Mockito.when(searchRequest.setMaxResults(anyLong())).thenReturn(searchRequest);
        Mockito.when(searchRequest.setKey(anyString())).thenReturn(searchRequest);

        Mockito.when(searchRequest.execute()).thenReturn(searchResponse);
        SearchResult mockResult = mock(SearchResult.class, RETURNS_DEEP_STUBS);

        Mockito.when(searchResponse.getItems()).thenReturn(Collections.emptyList());

        List<YouTubeVideo> videos = youtubeSearch.Search("sampleSearch", "home");
        assertTrue(videos.isEmpty());
    }

    /* @author: srinu.kesari */
    @Test
    public void testSearch_HashTagPage_EmptyResult() throws IOException {

        YouTube youtubeService = mock(YouTube.class);
        YouTube.Search search = mock(YouTube.Search.class);

        YouTube.Search.List searchRequest = mock(YouTube.Search.List.class);
        SearchListResponse searchResponse = mock(SearchListResponse.class);



        Mockito.when(youtubeService.search()).thenReturn(search);
        Mockito.when(search.list("snippet")).thenReturn(searchRequest);

        Mockito.when(searchRequest.setQ(anyString())).thenReturn(searchRequest);
        Mockito.when(searchRequest.setType(anyString())).thenReturn(searchRequest);
        Mockito.when(searchRequest.setMaxResults(anyLong())).thenReturn(searchRequest);
        Mockito.when(searchRequest.setKey(anyString())).thenReturn(searchRequest);

        Mockito.when(searchRequest.execute()).thenReturn(searchResponse);
        SearchResult mockResult = mock(SearchResult.class, RETURNS_DEEP_STUBS);

        Mockito.when(searchResponse.getItems()).thenReturn(Collections.emptyList());

        List<YouTubeVideo> videos = youtubeSearch.Search("sampleSearch", "hashTag");
        assertTrue(videos.isEmpty());
    }

    /* @author: srinu.kesari */
    @Test
    public void testSearch_WithNoRequest() throws IOException {
        List<YouTubeVideo> videos = youtubeSearch.Search("noResults", "nopage");
        assertTrue(videos.isEmpty());
    }

    /* @author: sahithi */
    @Test
    public void testSearch_TagsPage() throws IOException {
        YouTube.Videos.List videoRequest = mock(YouTube.Videos.List.class);
        VideoListResponse videoResponse = mock(VideoListResponse.class);
        Mockito.when(youtubeService.videos().list("snippet")).thenReturn(videoRequest);
        Mockito.when(videoRequest.setId(anyString())).thenReturn(videoRequest);
        Mockito.when(videoRequest.setMaxResults(anyLong())).thenReturn(videoRequest);
        Mockito.when(videoRequest.setKey(anyString())).thenReturn(videoRequest);
        Mockito.when(videoRequest.execute()).thenReturn(videoResponse);
        Video mockVideo = mock(Video.class);
        VideoSnippet mockSnippet = mock(VideoSnippet.class);
        Mockito.when(mockVideo.getSnippet()).thenReturn(mockSnippet);
        Mockito.when(mockSnippet.getTitle()).thenReturn("Test Video Title");
        Mockito.when(mockSnippet.getChannelTitle()).thenReturn("Test Channel");
        Mockito.when(mockSnippet.getDescription()).thenReturn("Test Description");
        Mockito.when(mockSnippet.getTags()).thenReturn(Collections.singletonList("Test Tag"));
        
        ThumbnailDetails mockThumbnailDetails = mock(ThumbnailDetails.class);
        Thumbnail mockThumbnail = mock(Thumbnail.class);
        Mockito.when(mockThumbnailDetails.getDefault()).thenReturn(mockThumbnail);
        Mockito.when(mockThumbnail.getUrl()).thenReturn("http://example.com/thumbnail.jpg");
        Mockito.when(mockSnippet.getThumbnails()).thenReturn(mockThumbnailDetails);

        Mockito.when(videoResponse.getItems()).thenReturn(Collections.singletonList(mockVideo));

        List<YouTubeVideo> videos = youtubeSearch.Search("testTag", "tags");

        assertNotNull(videos);
        assertEquals(1, videos.size());
        
        YouTubeVideo youtubeVideo = videos.get(0);
        assertEquals("Test Video Title", youtubeVideo.getTitle());
        assertEquals("Test Channel", youtubeVideo.getChannel());
        assertEquals("Test Description", youtubeVideo.getDescription());
        assertEquals("Test Tag", youtubeVideo.getTags().get(0));
        assertEquals("http://example.com/thumbnail.jpg", youtubeVideo.getThumbnailUrl());
    }

    /* @author: sahithi */
    @Test
    public void testSearch_TagsPage_EmptyResult() throws IOException {
        YouTube.Videos.List videoRequest = mock(YouTube.Videos.List.class);
        VideoListResponse videoResponse = mock(VideoListResponse.class);
        Mockito.when(youtubeService.videos().list("snippet")).thenReturn(videoRequest);
        Mockito.when(videoRequest.setId(anyString())).thenReturn(videoRequest);
        Mockito.when(videoRequest.setMaxResults(anyLong())).thenReturn(videoRequest);
        Mockito.when(videoRequest.setKey(anyString())).thenReturn(videoRequest);
        Mockito.when(videoRequest.execute()).thenReturn(videoResponse);

        Mockito.when(videoResponse.getItems()).thenReturn(Collections.emptyList());

        List<YouTubeVideo> videos = youtubeSearch.Search("nonExistentTag", "tags");

        assertNotNull(videos); 
        assertTrue(videos.isEmpty()); 
    }

    /* @author: sahithi */
    @Test
    public void testFetchFullDescriptions_SingleVideo() throws IOException {
        List<String> videoIds = Collections.singletonList("testVideoId");

        YouTube.Videos.List videoRequest = mock(YouTube.Videos.List.class);
        VideoListResponse videoResponse = mock(VideoListResponse.class);

        Mockito.when(youtubeService.videos().list("snippet")).thenReturn(videoRequest);
        Mockito.when(videoRequest.setId(anyString())).thenReturn(videoRequest);
        Mockito.when(videoRequest.setKey(anyString())).thenReturn(videoRequest);
        Mockito.when(videoRequest.execute()).thenReturn(videoResponse);

        Video mockVideo = mock(Video.class);
        VideoSnippet mockSnippet = mock(VideoSnippet.class); 

        Mockito.when(mockVideo.getSnippet()).thenReturn(mockSnippet);
        Mockito.when(mockSnippet.getTitle()).thenReturn("Test Video Title");
        Mockito.when(mockSnippet.getChannelTitle()).thenReturn("Test Channel");  
        Mockito.when(mockSnippet.getDescription()).thenReturn("Test Description");
        ThumbnailDetails mockThumbnailDetails = mock(ThumbnailDetails.class);
        Thumbnail mockThumbnail = mock(Thumbnail.class);
        Mockito.when(mockThumbnailDetails.getDefault()).thenReturn(mockThumbnail);
        Mockito.when(mockThumbnail.getUrl()).thenReturn("http://example.com/thumbnail.jpg");
        Mockito.when(mockSnippet.getThumbnails()).thenReturn(mockThumbnailDetails);
        Mockito.when(mockSnippet.getTags()).thenReturn(Collections.singletonList("Test Tag"));
        Mockito.when(videoResponse.getItems()).thenReturn(Collections.singletonList(mockVideo));

        List<YouTubeVideo> videos = youtubeSearch.fetchFullDescriptions(videoIds);

        assertNotNull(videos);
        assertEquals(1, videos.size());
        YouTubeVideo youtubeVideo = videos.get(0);
        assertEquals("Test Video Title", youtubeVideo.getTitle());
        assertEquals("Test Channel", youtubeVideo.getChannel());
        assertEquals("Test Description", youtubeVideo.getDescription());
        assertEquals("Test Tag", youtubeVideo.getTags().get(0));
        assertEquals("http://example.com/thumbnail.jpg", youtubeVideo.getThumbnailUrl());  
    }

    /* @author: sahithi */
    @Test
    public void testFetchFullDescriptions_NoVideosFound() throws IOException {
        List<String> videoIds = Collections.singletonList("nonExistentVideoId");
        YouTube.Videos.List videoRequest = mock(YouTube.Videos.List.class);
        VideoListResponse videoResponse = mock(VideoListResponse.class);
        Mockito.when(youtubeService.videos().list("snippet")).thenReturn(videoRequest);
        Mockito.when(videoRequest.setId(anyString())).thenReturn(videoRequest);
        Mockito.when(videoRequest.setKey(anyString())).thenReturn(videoRequest);
        Mockito.when(videoRequest.execute()).thenReturn(videoResponse);
        Mockito.when(videoResponse.getItems()).thenReturn(Collections.emptyList());
        List<YouTubeVideo> videos = youtubeSearch.fetchFullDescriptions(videoIds);
        assertNotNull(videos);
        assertTrue(videos.isEmpty());
    }
}
