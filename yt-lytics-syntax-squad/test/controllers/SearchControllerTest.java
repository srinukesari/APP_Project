import controllers.*;
import models.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static org.junit.Assert.fail;

public class SearchControllerTest {

    @Mock
    private YouTubeSearch youTubeSearch;

    @Mock
    private FormFactory formFactory;

    @Mock
    private MessagesApi messagesApi;

    @InjectMocks
    private SearchController searchController;

    private Http.Request request;
    private List<SearchResults> displayResults;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        request = Mockito.mock(Http.Request.class);
        displayResults = new ArrayList<>();
    }

//    @Test
//    public void testSearchFormhasErrors() {
//        Form<Search> searchForm = Mockito.mock(Form.class);
//        Mockito.when(searchForm.hasErrors()).thenReturn(true);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//
//        Result result = searchController.search(request);
//        assertEquals(400, result.status());
//    }
//
//    @Test
//    public void testSearchFormisNull() {
//        Form<Search> searchForm = Mockito.mock(Form.class);
//        Mockito.when(searchForm.hasErrors()).thenReturn(false);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//        Mockito.when(searchForm.get()).thenReturn(null);
//
//        Result result = searchController.search(request);
//        assertEquals(OK, result.status());
//    }

//    @Test
//    public void testSearchFormisEmpty() {
//        Search searchData = new Search();
//        searchData.setKey("");
//
//        Form<Search> searchForm = Mockito.mock(Form.class);
//        Mockito.when(searchForm.hasErrors()).thenReturn(false);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//        Mockito.when(searchForm.get()).thenReturn(searchData);
//
//        Result result = searchController.search(request);
//        assertEquals(OK, result.status());
//    }

    @Test
    public void testProfileChannelNameMissing() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/profile");

        Result result = searchController.profile(request.build());

        assertEquals(BAD_REQUEST, result.status());
        assertEquals("ChannelName not provided", contentAsString(result));
    }

    @Test
    public void testProfileChannelNameProvided() {
        String channelName = "TestChannel";
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/profile?channel=" + channelName);

        List<YouTubeVideo> mockVideos = new ArrayList<>();
        mockVideos.add(new YouTubeVideo("Id1", "title1", "TestChannel", "description1", "thumbnail1", null));
        mockVideos.add(new YouTubeVideo("Id2", "title2", "TestChannel", "description2", "thumbnail2", null));

        try{
            Mockito.when(youTubeSearch.Search(channelName, "profile")).thenReturn(mockVideos);

            Result result = searchController.profile(request.build());

            String content = contentAsString(result);

            assertEquals(OK, result.status());

            for (YouTubeVideo video : mockVideos) {
                assertTrue("Expected video title in the html", content.contains(video.getTitle()));
                assertTrue("Expected description in the html", content.contains(video.getDescription()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException thrown: " + e.getMessage());
        }
    }

    @Test
    public void testTagsVideoIdandHashTagMissing() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/tags");

        Result result = searchController.tags(request.build());

        assertEquals(BAD_REQUEST, result.status());
        assertEquals("videoId/ hashTag not provided", contentAsString(result));
    }

    @Test
    public void testTagsContainsVideoId() {
        String testVideoId = "testVideoId"; // Example channel name

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/tags?videoId="+testVideoId);

        List<YouTubeVideo> mockVideos = new ArrayList<>();
        mockVideos.add(new YouTubeVideo(
                "Id1", "title1", "TestChannel", "description1", "thumbnail1",
                Arrays.asList("tag1", "tag2")));

        try {
            Mockito.when(youTubeSearch.Search(testVideoId, "tags")).thenReturn(mockVideos);

            Result result = searchController.tags(request.build());

            String content = contentAsString(result);

            // System.out.println("check here -------->>>>>>"+content);
            assertEquals(OK, result.status());
            for (YouTubeVideo video : mockVideos) {
                assertTrue("Expected video title in the html", content.contains(video.getTitle()));
                assertTrue("Expected video description in the html", content.contains(video.getDescription()));
                assertTrue("Expected channel Name in the html", content.contains(video.getChannel()));
                for(String tag: video.getTags()){
                    assertTrue("Expected tags in the html", content.contains(tag));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            fail("IOException thrown: " + e.getMessage());
        }
    }


    @Test
    public void testTagsContainsHashTag() {
        String testHashTag = "testHashTag"; // Example channel name

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/tags?hashTag="+testHashTag);

        List<YouTubeVideo> mockVideos = new ArrayList<>();
        mockVideos.add(new YouTubeVideo("Id1", "title1", "channel1", "description1", "thumbnail1", null));
        mockVideos.add(new YouTubeVideo("Id2", "title2", "channel2", "description2", "thumbnail2", null));
        mockVideos.add(new YouTubeVideo("Id3", "title3", "channel3", "description3", "thumbnail3", null));

        try {
            Mockito.when(youTubeSearch.Search(testHashTag, "hashTag")).thenReturn(mockVideos);

            Result result = searchController.tags(request.build());

            String content = contentAsString(result);

            assertEquals(OK, result.status());
            for (YouTubeVideo video : mockVideos) {
                assertTrue("Expected video title in the html", content.contains(video.getTitle()));
                assertTrue("Expected video description in the html", content.contains(video.getDescription()));
                assertTrue("Expected channel Name in the html", content.contains(video.getChannel()));
            }
        }catch (IOException e){
            e.printStackTrace();
            fail("IOException thrown: " + e.getMessage());
        }
    }

    @Test
    public void testDisplayStatsSearchFound() {
        String searchTerm = "testTerm";
        List<YouTubeVideo> mockVideos = new ArrayList<>();
        mockVideos.add(new YouTubeVideo("Id1", "TestTerm Video One", "channel1", "This is a testTerm video description with some common words.", "thumbnail1", Arrays.asList("tag1", "tag2")));
        mockVideos.add(new YouTubeVideo("Id2", "TestTerm Video Two", "channel1", "Another description for a testTerm video, also with testTerm and words.", "thumbnail2", Arrays.asList("tag3", "tag4")));

        SearchResults mockSearchResults = new SearchResults(searchTerm, mockVideos);
        searchController.displayResults.add(mockSearchResults);  
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/ytlytics/morestats?searchTerms=" + searchTerm);

        Result result = searchController.displayStats(searchTerm);
        assertEquals(OK, result.status());
        String content = contentAsString(result);
        // System.out.println("check here -------->>>>>>" + content);
        String normalizedContent = content.replaceAll("\\s+", " ");
        // System.out.println("check here 2 -------->>>>>>" + normalizedContent);
        assertTrue("The content should contain the word 'testterm' with frequency '5'", normalizedContent.contains("<td>testterm</td> <td>5</td>"));
        assertTrue("The content should contain the word 'video' with frequency '4'", normalizedContent.contains("<td>video</td> <td>4</td>"));
        assertTrue("The content should contain the word 'words' with frequency '2'", normalizedContent.contains("<td>words</td> <td>2</td>"));
        assertTrue("The content should contain the word 'description' with frequency '2'", normalizedContent.contains("<td>description</td> <td>2</td>"));
    }

    @Test
    public void testDisplayStatsSearchNotFound() {
        String searchTerm = "nonExistentTerm";
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/ytlytics/morestats?searchTerms=" + searchTerm);

        Result result = searchController.displayStats(searchTerm);
        assertEquals(BAD_REQUEST, result.status());
        String content = contentAsString(result);
        assertTrue("The content should contain an error message when no results are found", content.contains("No search results found for the given terms."));
    }


    @Test
    public void testDisplayStatsWithEmptyResults() {
        String searchTerm = "validSearchTermWithNoResults";
        searchController.displayResults.clear();
        List<YouTubeVideo> mockVideos = new ArrayList<>();
        SearchResults mockSearchResults = new SearchResults(searchTerm, mockVideos);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/ytlytics/morestats?searchTerms=" + searchTerm);
        Result result = searchController.displayStats(searchTerm);
        assertEquals(BAD_REQUEST, result.status());
        String content = contentAsString(result);
        System.out.println("check here ----->" + content);
        assertTrue(content.contains("No search results found for the given terms."));
    }
    
}