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

    @Before
    public void setUp() {
        youtubeService = mock(YouTube.class, RETURNS_DEEP_STUBS);
        youtubeSearch = new YouTubeSearch(youtubeService);
    }

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

        YouTube.Search.List result = YouTubeSearch.getSearchRequestforProfile(youtubeService, "testChannel");

        verify(searchChannelRequest).setChannelId("testChannelId");

        assertNotNull(result);
    }

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

    @Test
    public void testGetSearchRequestforHome() throws IOException {
        YouTube.Search.List searchRequest = YouTubeSearch.getSearchRequestforHome(youtubeService, "sampleSearch");
        assertNotNull(searchRequest);
    }

    @Test
    public void testGetSearchRequestforTags() throws IOException {
        YouTube.Search.List searchRequest = YouTubeSearch.getSearchRequestforTags(youtubeService, "sampleSearch");
        assertNotNull(searchRequest);
    }

    @Test
    public void testGetVideoRequestforTags() throws IOException {
        YouTube.Videos.List searchRequest = YouTubeSearch.getVideoRequestforTags(youtubeService, "sampleSearch");
        assertNotNull(searchRequest);
    }

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

        List<YouTubeVideo> videos = youtubeSearch.Search("sampleSearch", "hashtag");
        assertTrue(videos.isEmpty());
    }

//    @Test
//    public void testSearch_NoResults() throws IOException {
//        YouTube.Search.List searchRequest = mock(YouTube.Search.List.class);
//        SearchListResponse searchResponse = mock(SearchListResponse.class);
//
//        Mockito.when(youtubeService.search().list("snippet")).thenReturn(searchRequest);
//        Mockito.when(searchRequest.setQ(anyString())).thenReturn(searchRequest);
//        Mockito.when(searchRequest.execute()).thenReturn(searchResponse);
//        Mockito.when(searchResponse.getItems()).thenReturn(Collections.emptyList());
//
//        List<YouTubeVideo> videos = youtubeSearch.Search("noResults", "home");
//
//        System.out.println("check kkoooooooo"+videos);
//        assertTrue(videos.isEmpty());
//    }

    // Similar tests can be created for "tags", "profile", and "hashTag" page cases
}
