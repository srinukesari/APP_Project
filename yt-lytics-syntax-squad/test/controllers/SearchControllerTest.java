//package tests;
//import controllers.*;
//import models.*;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.mockito.ArgumentMatchers;
//
//import play.data.Form;
//import play.data.FormFactory;
//import play.i18n.MessagesApi;
//import play.i18n.Messages;
//import play.mvc.Http;
//import play.mvc.Result;
//import play.test.Helpers;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Arrays;
//import java.io.IOException;
//import java.util.Optional;
//
//import org.mockito.Mock;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static play.mvc.Http.Status.BAD_REQUEST;
//import static play.mvc.Http.Status.OK;
//import static play.test.Helpers.contentAsString;
//import static org.junit.Assert.fail;
//
//import java.util.concurrent.CompletableFuture;
//
///**
// * Test class for the {@link SearchController}.
// * This class contains unit tests for different scenarios of the search functionality.
// * It uses Mockito to mock dependencies and test the behavior of the controller.
// */
//public class SearchControllerTest {
//
//    @Mock
//    private YouTubeSearch youTubeSearch;
//
//    @Mock
//    private FormFactory formFactory;
//
//    @Mock
//    private MessagesApi messagesApi;
//
//    @Mock
//    private Messages messages;
//
//    @InjectMocks
//    private SearchController searchController;
//
//    private Http.Request request;
//    private List<SearchResults> displayResults;
//
//    /**
//     * Setup method to initialize mocks and prepare common test data before each test.
//     * It mocks the dependencies and sets up a mock request.
//     */
//    @Before
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        request = Mockito.mock(Http.Request.class);
//        displayResults = new ArrayList<>();
//    }
//
//    /**
//     * Test case to verify the behavior when the search form is null.
//     * The test expects a 400 Bad Request response when the form is not provided.
//     *
//     * @author srinu.kesari
//     */
//    @Test
//    public void testSearchFormisNull() {
//        Form<Search> searchForm = Mockito.mock(Form.class);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//        Mockito.when(searchForm.bindFromRequest(request)).thenReturn(null);
//        CompletableFuture<Result> results = searchController.search(request);
//        Result result = results.join();
//        assertEquals(400, result.status());
//    }
//
//    /**
//     * Test case to verify the behavior when the search form has validation errors.
//     * The test expects a 400 Bad Request response when the form has errors.
//     *
//     * @author srinu.kesari
//     */
//    @Test
//    public void testSearchFormhasErrors() {
//        Form<Search> searchForm = Mockito.mock(Form.class);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//        Mockito.when(searchForm.bindFromRequest(request)).thenReturn(searchForm);
//        Mockito.when(searchForm.hasErrors()).thenReturn(true);
//        CompletableFuture<Result> results = searchController.search(request);
//        Result result = results.join();
//        assertEquals(400, result.status());
//    }
//
//    /**
//     * Test case to verify the behavior when the search form's key is null.
//     * The test expects a 400 Bad Request response when the search form's key is not provided.
//     *
//     * @author srinu.kesari
//     */
//    @Test
//    public void testSearchFormKeyisNull() {
//        Form<Search> searchForm = Mockito.mock(Form.class);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//        Mockito.when(searchForm.bindFromRequest(request)).thenReturn(searchForm);
//        Mockito.when(searchForm.hasErrors()).thenReturn(false);
//        Mockito.when(searchForm.get()).thenReturn(null);
//        CompletableFuture<Result> results = searchController.search(request);
//        Result result = results.join();
//        assertEquals(400, result.status());
//    }
//
//    /**
//     * Test case to verify the behavior when an exception occurs while calling the YouTube API.
//     * Specifically, it tests that an {@link IOException} (e.g., due to an invalid API key) results in
//     * a 400 Bad Request response.
//     *
//     * @author Aniket
//     */
//    @Test
//    public void testSearchFormhasYoutubeApiException() {
//
//        Search searchData = new Search();
//        searchData.setKey("testSearchKey");
//
//        Form<Search> searchForm = Mockito.mock(Form.class);
//
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//
//        Mockito.when(searchForm.bindFromRequest(request)).thenReturn(searchForm);
//        Mockito.when(searchForm.hasErrors()).thenReturn(false);
//        Mockito.when(searchForm.get()).thenReturn(searchData);
//        Mockito.when(messagesApi.preferred(request)).thenReturn(messages);
//
//        try{
//            Mockito.when(youTubeSearch.Search("testSearchKey", "home")).
//                    thenThrow(new IOException("Invalid API Key"));
//            CompletableFuture<Result> results = searchController.search(request);
//            Result result = results.join();
//
//            String content = contentAsString(result);
//
//            assertEquals(400, result.status());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Test case to verify the behavior when there are more than expected YouTube videos
//     * and an exception occurs. Specifically, it expects a 400 Bad Request response.
//     *
//     * @author aniket
//     */
//    @Test
//    public void testSearchFormhasMoreVideoswithException() {
//
//        Search searchData = new Search();
//        searchData.setKey("testSearchKey");
//
//        Form<Search> searchForm = Mockito.mock(Form.class);
//
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//
//        Mockito.when(searchForm.bindFromRequest(request)).thenReturn(searchForm);
//        Mockito.when(searchForm.hasErrors()).thenReturn(false);
//        Mockito.when(searchForm.get()).thenReturn(searchData);
//        Mockito.when(messagesApi.preferred(request)).thenReturn(messages);
//
//
//        List<YouTubeVideo> mockVideos = new ArrayList<>();
//        for (int i = 1; i < 17; i++) {
//            if (i % 2 == 0) {
//                int j = 2;
//                mockVideos.add(new YouTubeVideo("Id" + j, "title" + j, "TestChannel", "description" + j, "thumbnail" + j, null));
//            } else {
//                int j = 1;
//                mockVideos.add(new YouTubeVideo("Id" + j, "title" + j, "TestChannel", "description" + j, "thumbnail" + j, null));
//            }
//        }
//
//        try{
//            Mockito.when(youTubeSearch.Search("testSearchKey", "home")).thenReturn(mockVideos);
//            Mockito.when(youTubeSearch.fetchFullDescriptions(ArgumentMatchers.anyList())).thenReturn(mockVideos);
//            CompletableFuture<Result> results = searchController.search(request);
//            Result result = results.join();
//            String content = contentAsString(result);
//            assertEquals(400, result.status());
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail("IOException thrown: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Test case to verify the behavior when there are YouTube videos without descriptions
//     * or with descriptions that are invalid. It expects a 400 Bad Request response.
//     *
//     * @author aniket
//     */
//    @Test
//    public void testSearchFormhasNoDescriptionwithException() {
//
//        Search searchData = new Search();
//        searchData.setKey("testSearchKey");
//
//        Form<Search> searchForm = Mockito.mock(Form.class);
//
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//        Mockito.when(formFactory.form(Search.class)).thenReturn(searchForm);
//
//        Mockito.when(searchForm.bindFromRequest(request)).thenReturn(searchForm);
//        Mockito.when(searchForm.hasErrors()).thenReturn(false);
//        Mockito.when(searchForm.get()).thenReturn(searchData);
//        Mockito.when(messagesApi.preferred(request)).thenReturn(messages);
//
//
//        List<YouTubeVideo> mockVideos = new ArrayList<>();
//        mockVideos.add(new YouTubeVideo("Id1", "title1", "TestChannel", "    ", "thumbnail1", null));
//        mockVideos.add(new YouTubeVideo("Id2", "title2", "TestChannel", "shorts.", "thumbnail2", null));
//        mockVideos.add(new YouTubeVideo("Id1", "title1", "TestChannel",
//                "THIS DESCRIPTION IS GREATER THAN 50 LENGTH,SO TYPING BLAW BLAW BLAW BLAW BLAW BLACK SHEEP BLAW SHEEP",
//                "thumbnail1", null));
//        for (int i = 1; i < 14; i++) {
//            if (i % 2 == 0) {
//                int j = 2;
//                mockVideos.add(new YouTubeVideo("Id" + j, "title" + j, "TestChannel", "description" + j, "thumbnail" + j, null));
//            }else{
//                int j = 1;
//                mockVideos.add(new YouTubeVideo("Id" + j, "title" + j, "TestChannel", "description" + j, "thumbnail" + j, null));
//            }
//        }
//        try{
//            Mockito.when(youTubeSearch.Search("testSearchKey", "home")).thenReturn(mockVideos);
//            Mockito.when(youTubeSearch.fetchFullDescriptions(ArgumentMatchers.anyList())).thenReturn(mockVideos);
//
//            Form.Field mockField = Mockito.mock(Form.Field.class);
//
//            Mockito.when(searchForm.field("key")).thenReturn(mockField);
//            Mockito.when(mockField.name()).thenReturn(Optional.of("key"));
//            Mockito.when(mockField.value()).thenReturn(Optional.of("testSearchKey"));
//            CompletableFuture<Result> results = searchController.search(request);
//            Result result = results.join();
//
//            String content = contentAsString(result);
//
//            assertEquals(400, result.status());
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail("IOException thrown: " + e.getMessage());
//        }
//    }
//
//
//    /**
//     * Test case to verify the behavior when the channel name is missing in the profile request.
//     * It expects a 400 Bad Request response with an appropriate error message.
//     *
//     * @author sushmitha
//     */
//    @Test
//    public void testProfileChannelNameMissing() {
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method("GET")
//                .uri("/profile");
//
//        CompletableFuture<Result> futureResult = searchController.profile(request.build());
//        Result result = futureResult.join();
//        assertEquals(BAD_REQUEST, result.status());
//        assertEquals("ChannelName not provided", contentAsString(result));
//    }
//
//    /**
//     * Test case to verify the behavior when the channel name is provided in the profile request.
//     * It expects a 200 OK response and checks that video titles and descriptions are in the response content.
//     *
//     * @author sushmitha
//     */
//    @Test
//    public void testProfileChannelNameProvided() {
//        String channelName = "TestChannel";
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method("GET")
//                .uri("/profile?channel=" + channelName);
//
//        List<YouTubeVideo> mockVideos = new ArrayList<>();
//        mockVideos.add(new YouTubeVideo("Id1", "title1", "TestChannel", "description1", "thumbnail1", null));
//        mockVideos.add(new YouTubeVideo("Id2", "title2", "TestChannel", "description2", "thumbnail2", null));
//
//        try{
//            Mockito.when(youTubeSearch.Search(channelName, "profile")).thenReturn(mockVideos);
//            CompletableFuture<Result> futureResult = searchController.profile(request.build());
//            Result result = futureResult.join();
//
//            String content = contentAsString(result);
//
//            assertEquals(OK, result.status());
//
//            for (YouTubeVideo video : mockVideos) {
//                assertTrue("Expected video title in the html", content.contains(video.getTitle()));
//                assertTrue("Expected description in the html", content.contains(video.getDescription()));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail("IOException thrown: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Test case for the profile page when there are more than 10 videos.
//     * It mocks the response from the YouTube API and checks if the video titles and descriptions
//     * are correctly displayed in the response content.
//     *
//     * @author aniket
//     */
//    @Test
//    public void testProfileMoreThan10Videos() {
//        String channelName = "TestChannel";
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method("GET")
//                .uri("/profile?channel=" + channelName);
//
//        List<YouTubeVideo> mockVideos = new ArrayList<>();
//        for (int i = 1; i < 17; i++) {
//            if (i % 2 == 0) {
//                int j = 2;
//                mockVideos.add(new YouTubeVideo("Id" + j, "title" + j, "TestChannel", "description" + j, "thumbnail" + j, null));
//            } else {
//                int j = 1;
//                mockVideos.add(new YouTubeVideo("Id" + j, "title" + j, "TestChannel", "description" + j, "thumbnail" + j, null));
//            }
//        }
//
//        try{
//            Mockito.when(youTubeSearch.Search(channelName, "profile")).thenReturn(mockVideos);
//            CompletableFuture<Result> futureResult = searchController.profile(request.build());
//            Result result = futureResult.join();
//            String content = contentAsString(result);
//            assertEquals(OK, result.status());
//
//            for (YouTubeVideo video : mockVideos) {
//                assertTrue("Expected video title in the html", content.contains(video.getTitle()));
//                assertTrue("Expected description in the html", content.contains(video.getDescription()));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail("IOException thrown: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Test case for handling an invalid API key when fetching profile data.
//     * It verifies that a BAD_REQUEST response is returned and the error message is displayed.
//     *
//     * @author srinu.kesari
//     */
//    @Test
//    public void testProfilePageExceptionInvalidAPIKey() {
//        String channelName = "TestChannel";
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method("GET")
//                .uri("/profile?channel=" + channelName);
//
//        try{
//            Mockito.when(youTubeSearch.Search(channelName, "profile")).
//                    thenThrow(new IOException("Invalid API Key"));
//            CompletableFuture<Result> futureResult = searchController.profile(request.build());
//            Result result = futureResult.join();
//
//            String content = contentAsString(result);
//
//            assertEquals(BAD_REQUEST, result.status());
//            assertTrue("Expected Invalid API Key", content.contains("Invalid API Key"));
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("exception happend in tests"+e);
//        }
//    }
//
//    /**
//     * Test case for handling an exception when the API key is invalid during tag search.
//     * It ensures the system returns a BAD_REQUEST response and displays the error message.
//     *
//     * @author sahithi
//     */
//    @Test
//    public void testTagsExceptionInvalidAPIKey() {
//        String videoId = "TestVideo";
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method("GET")
//                .uri("/tag?videoId=" + videoId);
//
//        try{
//            Mockito.when(youTubeSearch.Search(videoId, "tags")).
//                    thenThrow(new IOException("Invalid API Key"));
//            CompletableFuture<Result> futureResult = searchController.tags(request.build());
//            Result result = futureResult.join();
//
//            String content = contentAsString(result);
//
//            assertEquals(BAD_REQUEST, result.status());
//            assertTrue("Expected Invalid API Key", content.contains("Invalid API Key"));
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("exception happend in tests"+e);
//        }
//    }
//
//
//    /**
//     * Test case to verify that the system returns an error when no videoId or hashTag is provided
//     * for a tag search request.
//     *
//     * @author sahithi
//     */
//    @Test
//    public void testTagsVideoIdandHashTagMissing() {
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method("GET")
//                .uri("/tags");
//        CompletableFuture<Result> futureResult = searchController.tags(request.build());
//        Result result = futureResult.join();
//
//        assertEquals(BAD_REQUEST, result.status());
//        assertEquals("videoId/ hashTag not provided", contentAsString(result));
//    }
//
//    /**
//     * Test case to check that the videoId tag search returns the expected video details.
//     * It mocks the response and verifies that the video details, including tags, are correctly
//     * displayed in the HTML content.
//     *
//     * @author sahithi
//     */
//    @Test
//    public void testTagsContainsVideoId() {
//        String testVideoId = "testVideoId"; // Example channel name
//
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method("GET")
//                .uri("/tags?videoId="+testVideoId);
//
//        List<YouTubeVideo> mockVideos = new ArrayList<>();
//        mockVideos.add(new YouTubeVideo(
//                "Id1", "title1", "TestChannel", "description1", "thumbnail1",
//                Arrays.asList("tag1", "tag2")));
//
//        try {
//            Mockito.when(youTubeSearch.Search(testVideoId, "tags")).thenReturn(mockVideos);
//            CompletableFuture<Result> futureResult = searchController.tags(request.build());
//            Result result = futureResult.join();
//
//            String content = contentAsString(result);
//
//            assertEquals(OK, result.status());
//            for (YouTubeVideo video : mockVideos) {
//                assertTrue("Expected video title in the html", content.contains(video.getTitle()));
//                assertTrue("Expected video description in the html", content.contains(video.getDescription()));
//                assertTrue("Expected channel Name in the html", content.contains(video.getChannel()));
//                for(String tag: video.getTags()){
//                    assertTrue("Expected tags in the html", content.contains(tag));
//                }
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//            fail("IOException thrown: " + e.getMessage());
//        }
//    }
//
//
//    /**
//     * Test case to check that the hashtag tag search returns the expected video details.
//     * It mocks the response and verifies that the video details are correctly displayed in the HTML.
//     *
//     * @author sahithi
//     */
//    @Test
//    public void testTagsContainsHashTag() {
//        String testHashTag = "testHashTag"; // Example channel name
//
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method("GET")
//                .uri("/tags?hashTag="+testHashTag);
//
//        List<YouTubeVideo> mockVideos = new ArrayList<>();
//        mockVideos.add(new YouTubeVideo("Id1", "title1", "channel1", "description1", "thumbnail1", null));
//        mockVideos.add(new YouTubeVideo("Id2", "title2", "channel2", "description2", "thumbnail2", null));
//        mockVideos.add(new YouTubeVideo("Id3", "title3", "channel3", "description3", "thumbnail3", null));
//
//        try {
//            Mockito.when(youTubeSearch.Search(testHashTag, "hashTag")).thenReturn(mockVideos);
//            CompletableFuture<Result> futureResult = searchController.tags(request.build());
//            Result result = futureResult.join();
//
//            String content = contentAsString(result);
//
//            assertEquals(OK, result.status());
//            for (YouTubeVideo video : mockVideos) {
//                assertTrue("Expected video title in the html", content.contains(video.getTitle()));
//                assertTrue("Expected video description in the html", content.contains(video.getDescription()));
//                assertTrue("Expected channel Name in the html", content.contains(video.getChannel()));
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//            fail("IOException thrown: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Test case to verify that the stats page displays the correct frequency of search terms found
//     * in the descriptions of the videos.
//     *
//     * @author sahithi
//     */
//    @Test
//    public void testDisplayStatsSearchFound() {
//        String searchTerm = "testTerm";
//        List<YouTubeVideo> mockVideos = new ArrayList<>();
//        mockVideos.add(new YouTubeVideo("Id1", "TestTerm Video One", "channel1",
//            "This is a testTerm video description with some common words.", "thumbnail1", Arrays.asList("tag1", "tag2")));
//        mockVideos.add(new YouTubeVideo("Id2", "TestTerm Video Two", "channel1",
//            "Another description for a testTerm video, also with testTerm and words.", "thumbnail2", Arrays.asList("tag3", "tag4")));
//        SearchResults mockSearchResults = new SearchResults(searchTerm, mockVideos);
//        searchController.morestatsResults.add(mockSearchResults);
//        try {
//            Mockito.when(youTubeSearch.Search(searchTerm, "home")).thenReturn(mockVideos);
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Exception while mocking YouTubeSearch");
//        }
//        CompletableFuture<Result> results = searchController.displayStats(searchTerm);
//        Result result = results.join();
//        assertEquals(OK, result.status());
//        String content = contentAsString(result);
//
//        String normalizedContent = content.replaceAll("\\s+", " ");
//        assertTrue("The content should contain the word 'testterm' with frequency '5'", normalizedContent.contains("<td>testterm</td> <td>5</td>"));
//        assertTrue("The content should contain the word 'video' with frequency '4'", normalizedContent.contains("<td>video</td> <td>4</td>"));
//        assertTrue("The content should contain the word 'words' with frequency '2'", normalizedContent.contains("<td>words</td> <td>2</td>"));
//        assertTrue("The content should contain the word 'description' with frequency '2'", normalizedContent.contains("<td>description</td> <td>2</td>"));
//    }
//
//    /**
//     * Test case for handling the scenario where no search results are found for the given search term.
//     * It verifies that a BAD_REQUEST status is returned and the appropriate error message is displayed.
//     *
//     * @author sahiti
//     */
//    @Test
//    public void testDisplayStatsSearchNotFound() {
//        String searchTerm = "nonExistentTerm";
//        CompletableFuture<Result> results = searchController.displayStats(searchTerm);
//        Result result = results.join();
//        assertEquals(BAD_REQUEST, result.status());
//        String content = contentAsString(result);
//        assertTrue("The content should contain an error message when no results are found", content.contains("No search results found for the given terms."));
//    }
//
//
//    /**
//     * Test case for handling the scenario where the search term results in an empty list of videos.
//     * It ensures that the system returns a BAD_REQUEST status and the appropriate message is shown.
//     *
//     * @author sahiti
//     */
//    @Test
//    public void testDisplayStatsWithEmptyResults() {
//        String searchTerm = "validSearchTermWithNoResults";
//        searchController.displayResults.clear();
//        List<YouTubeVideo> mockVideos = new ArrayList<>();
//        SearchResults mockSearchResults = new SearchResults(searchTerm, mockVideos);
//        CompletableFuture<Result> results = searchController.displayStats(searchTerm);
//        Result result = results.join();
//        assertEquals(BAD_REQUEST, result.status());
//        String content = contentAsString(result);
//        assertTrue(content.contains("No search results found for the given terms."));
//    }
//
//    /**
//     * Test case to calculate the average Flesch-Kincaid Grade Level of multiple video descriptions.
//     * It verifies that the system correctly calculates the average based on the Flesch-Kincaid Grade Level
//     * for each video's description.
//     *
//     * @author sahiti
//     */
//    @Test
//    public void testCalculateAverageFleschKincaidGradeLevel() {
//        String description1 = "This is a test description. It should be long enough to calculate syllables and sentences.";
//        YouTubeVideo video1 = new YouTubeVideo("Id1", "Video 1", "channel1", description1, "thumbnail1", Arrays.asList("tag1", "tag2"));
//        String description2 = "This is another test description, providing more content for the Flesch-Kincaid calculation.";
//        YouTubeVideo video2 = new YouTubeVideo("Id2", "Video 2", "channel2", description2, "thumbnail2", Arrays.asList("tag3", "tag4"));
//        String description3 = "Short description.";
//        YouTubeVideo video3 = new YouTubeVideo("Id3", "Video 3", "channel3", description3, "thumbnail3", Arrays.asList("tag5", "tag6"));
//        List<YouTubeVideo> videos = Arrays.asList(video1, video2, video3);
//        CompletableFuture<Double> averageGradeLevels = searchController.calculateAverageFleschKincaidGradeLevel(videos);
//        double averageGradeLevel = averageGradeLevels.join();
//        double expectedAverage = (video1.getFleschKincaidGradeLevel() + video2.getFleschKincaidGradeLevel() + video3.getFleschKincaidGradeLevel()) / 3;
//        assertEquals("The average Flesch-Kincaid Grade Level should be correct", expectedAverage, averageGradeLevel, 0.01);
//    }
//
//    /**
//     * Test case to calculate the average Flesch Reading Ease Score of multiple video descriptions.
//     * It verifies that the system correctly calculates the average based on the Flesch Reading Ease Score
//     * for each video's description.
//     *
//     * @author sahiti
//     */
//    @Test
//    public void testCalculateAverageFleschReadingEaseScore() {
//        String description1 = "This is a test description. It should be long enough to calculate syllables and sentences.";
//        YouTubeVideo video1 = new YouTubeVideo("Id1", "Video 1", "channel1", description1, "thumbnail1", Arrays.asList("tag1", "tag2"));
//        String description2 = "This is another test description, providing more content for the Flesch-Kincaid calculation.";
//        YouTubeVideo video2 = new YouTubeVideo("Id2", "Video 2", "channel2", description2, "thumbnail2", Arrays.asList("tag3", "tag4"));
//        String description3 = "Short description.";
//        YouTubeVideo video3 = new YouTubeVideo("Id3", "Video 3", "channel3", description3, "thumbnail3", Arrays.asList("tag5", "tag6"));
//        List<YouTubeVideo> videos = Arrays.asList(video1, video2, video3);
//        CompletableFuture<Double> averageEaseScores = searchController.calculateAverageFleschReadingEaseScore(videos);
//        double averageEaseScore = averageEaseScores.join();
//        double expectedAverage = (video1.getFleschReadingEaseScore() + video2.getFleschReadingEaseScore() + video3.getFleschReadingEaseScore()) / 3;
//        assertEquals("The average Flesch-Kincaid Grade Level should be correct", expectedAverage, averageEaseScore, 0.01);
//    }
//}