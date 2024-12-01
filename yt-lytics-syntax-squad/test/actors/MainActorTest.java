import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.testkit.javadsl.TestKit;
import org.apache.pekko.stream.Materializer;
import models.YouTubeVideo;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import java.time.Duration;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

import models.*;
import controllers.*;
import actors.*;

/**
 * Test class for testing the behavior of the MainActor in an actor-based system.
 * 
 * This class uses JUnit and Mockito to test different scenarios in which the MainActor
 * processes various requests such as "unknown", "search", "tags", "hashTag", and "stats".
 * The tests simulate interactions with the actor and check the expected results by 
 * verifying the messages received by the test probe.
 */

public class MainActorTest {

    private ActorSystem system;
    private Materializer materializer;

    @Mock
    private YouTubeSearch mockYouTubeSearch;  // Mock YouTubeSearch

    private ActorRef mainActor; // Reference to the MainActor under test

    /**
     * Setup method that runs before each test.
     * Initializes the actor system, materializer, and mocks, and creates the MainActor instance.
     */
    @Before
    public void setUp() {
        system = ActorSystem.create();
        materializer = Materializer.createMaterializer(system);
        MockitoAnnotations.openMocks(this); // Initialize mocks
        mainActor = system.actorOf(MainActor.props(materializer, mockYouTubeSearch));
    }

    /**
     * Test case for handling an unknown request type.
     * This test checks that when the MainActor receives an "unknown" request,
     * it should respond with an appropriate message indicating the unknown request type.
     * @author srinu kesari
     */
    @Test
    public void testMainActorWithUnknownRequest() throws Exception {
        TestKit probe = new TestKit(system);

        JsonNode requestMsg = Json.newObject().put("path", "unknown").put("key", "testKey");
        mainActor.tell(requestMsg, probe.getRef());

        JsonNode expectedResponse = Json.toJson("Unknown request type");
        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
    }


    /**
     * Test case for verifying the behavior of the main actor when a search request is received.
     * <p>
     * This test mocks the behavior of the `YouTubeSearch` service to simulate a search request with a specific key and path ("search").
     * It then verifies that the actor returns the correct response in the form of a JSON object containing the following:
     * <ul>
     *   <li>The search path ("search")</li>
     *   <li>The search term used in the query ("sampleKey")</li>
     *   <li>The list of YouTube videos (mocked response)</li>
     *   <li>The average Flesch-Kincaid Grade Level (mocked as 0.0)</li>
     *   <li>The average Flesch Reading Ease Score (mocked as 0.0)</li>
     * </ul>
     * <p>
     * The test ensures that the main actor processes the search request correctly and responds with the expected JSON structure.
     *
     * @author aniket
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testMainActorWithSearchRequest() throws Exception {
        TestKit probe = new TestKit(system);
        List<YouTubeVideo> response = new ArrayList<>();
        List<String> videoIds = new ArrayList<>();

        response.add(new YouTubeVideo("new Id", "Test Video", "Test Channel", "shorts", "thumbnail1", null));
        videoIds.add("sampleVideoId");

        Mockito.when(mockYouTubeSearch.Search("sampleKey", "home"))
                .thenReturn(response);

        Mockito.when(mockYouTubeSearch.fetchFullDescriptions(Mockito.any()))
                .thenReturn(response);

        JsonNode requestMsg = Json.newObject().put("path", "search").put("key", "sampleKey");
        mainActor.tell(requestMsg, probe.getRef());

        ObjectNode expectedResponse = Json.newObject();
        JsonNode jsonResponseObject = Json.toJson(response);
        expectedResponse.put("path","search");
        expectedResponse.put("searchTerms","sampleKey");
        expectedResponse.put("youTubeVideosList",jsonResponseObject);
        expectedResponse.put("averageFleschKincaidGradeLevel",0.0);
        expectedResponse.put("averageFleschReadingEaseScore",121.22);

        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
    }

    /**
     * Test case for verifying the behavior of the main actor when a search request with an empty search key is received.
     * <p>
     * This test ensures that the actor correctly handles invalid input by rejecting a search request with an empty search key.
     * The actor should respond with an appropriate error message indicating that the search key cannot be empty.
     * <p>
     * The test validates that the actor:
     * <ul>
     *   <li>Receives the request with an empty "key" field.</li>
     *   <li>Returns the expected error message as a JSON object.</li>
     * </ul>
     * @author srinu.kesari
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testMainActorWithEmptySearchKeyRequest() throws Exception {
        TestKit probe = new TestKit(system);

        JsonNode requestMsg = Json.newObject().put("path", "search").put("key", "");
        mainActor.tell(requestMsg, probe.getRef());

        probe.expectMsgEquals(Duration.ofSeconds(5), Json.toJson("Search key cannot be empty"));
    }

    /**
     * Test case for verifying the behavior of the main actor when receiving a request with an empty ID.
     * This test simulates a scenario where the main actor receives a message containing a tag request with an empty search key.
     * It validates that the actor responds with an appropriate message indicating that the video ID or hash tag was not provided.
     * @author srinu.kesari
     * @throws Exception if any error occurs during the test execution.
     */
    @Test
    public void testMainActorWithTagRequestWithEmptyId() throws Exception {
        TestKit probe = new TestKit(system);
        List<YouTubeVideo> response = new ArrayList<>();
        response.add(new YouTubeVideo("new Id", "Test Video", "Test Channel", "", "thumbnail1", null));

        JsonNode requestMsg = Json.newObject().put("path", "hashTag").put("key", "");
        mainActor.tell(requestMsg, probe.getRef());

        probe.expectMsgEquals(Duration.ofSeconds(5), Json.toJson("videoId/ hashTag not provided"));
    }

    /**
     * Test case for handling a tags request.
     * This test simulates a request for retrieving videos based on tags and checks that the MainActor
     * correctly responds with the list of YouTube videos that match the tag.
     * @author srinu.kesari
     * @throws Exception if any error occurs during the test execution.
     */
    @Test
    public void testMainActorWithTagRequest() throws Exception {
        TestKit probe = new TestKit(system);
        List<YouTubeVideo> response = new ArrayList<>();
        response.add(new YouTubeVideo("new Id", "Test Video", "Test Channel", "", "thumbnail1", null));
        Mockito.when(mockYouTubeSearch.Search("SampleVideoId", "tags"))
                .thenReturn(response);

        JsonNode requestMsg = Json.newObject().put("path", "tags").put("key", "SampleVideoId");
        mainActor.tell(requestMsg, probe.getRef());

        ObjectNode expectedResponse = Json.newObject();
        JsonNode jsonResponseObject = Json.toJson(response);
        expectedResponse.put("path","tags");
        expectedResponse.put("youTubeVideosList",jsonResponseObject);
        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
    }

    /**
     * Test case for handling a hashtag request.
     * This test simulates a request for retrieving videos based on a hashtag and checks that the MainActor
     * responds with the list of YouTube videos that match the hashtag.
     * @author srinu.kesari
     * @throws Exception if any error occurs during the test execution.
     */
    @Test
    public void testMainActorWithHashTagRequest() throws Exception {
        TestKit probe = new TestKit(system);
        List<YouTubeVideo> response = new ArrayList<>();
        response.add(new YouTubeVideo("new Id", "Test Video", "Test Channel", "", "thumbnail1", null));
        Mockito.when(mockYouTubeSearch.Search("#SampleHashTag", "hashTag"))
                .thenReturn(response);

        JsonNode requestMsg = Json.newObject().put("path", "hashTag").put("key", "#SampleHashTag");
        mainActor.tell(requestMsg, probe.getRef());

        ObjectNode expectedResponse = Json.newObject();
        JsonNode jsonResponseObject = Json.toJson(response);
        expectedResponse.put("path","hashTag");
        expectedResponse.put("youTubeVideosList",jsonResponseObject);
        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
    }

    /**
     * Test case for handling a stats request.
     * This test checks that the MainActor processes the stats request correctly
     * @author sahiti
     * @throws Exception if any error occurs during the test execution.
     */
    @Test
    public void testMainActorWithStatsRequest() throws Exception {
        TestKit probe = new TestKit(system);
        List<YouTubeVideo> response = new ArrayList<>();
        response.add(new YouTubeVideo("new Id", "Test Video", "Test Channel", "", "thumbnail1", null));
        Mockito.when(mockYouTubeSearch.Search("#SampleHashTag", "hashTag"))
                .thenReturn(response);

        JsonNode requestMsg = Json.newObject().put("path", "stats").put("key", "statsample");
        mainActor.tell(requestMsg, probe.getRef());

        ObjectNode expectedResponse = Json.newObject();
        JsonNode jsonResponseObject = Json.toJson(response);
        expectedResponse.put("path","stats");
        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
    }

    /**
     * Test case for verifying the behavior of the main actor when handling a profile request.
     * This test simulates a scenario where the main actor receives a profile request containing a channel key.
     * It mocks the `YouTubeSearch` service to return a list of `YouTubeVideo` objects for the given channel,
     * and verifies that the actor responds with a correctly formatted JSON message containing the expected profile data.
     * @author sushmitha
     * @throws Exception if any error occurs during the test execution.
     */
    @Test
    public void testMainActorWithProfileRequest() throws Exception {
        TestKit probe = new TestKit(system);
        List<YouTubeVideo> response = new ArrayList<>();
        response.add(new YouTubeVideo("new Id", "Test Video", "Test Channel", "", "thumbnail1", null));
        Mockito.when(mockYouTubeSearch.Search("sampleChannel", "profile"))
                .thenReturn(response);

        JsonNode requestMsg = Json.newObject().put("path", "profile").put("key", "sampleChannel");
        mainActor.tell(requestMsg, probe.getRef());

        ObjectNode expectedResponse = Json.newObject();
        JsonNode jsonResponseObject = Json.toJson(response);
        expectedResponse.put("path","profile");
        expectedResponse.put("channel","sampleChannel");
        expectedResponse.put("youTubeVideosList",jsonResponseObject);
        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
    }
}
