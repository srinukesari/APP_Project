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
     */
    @Test
    public void testMainActorWithUnknownRequest() throws Exception {
        TestKit probe = new TestKit(system);

        JsonNode requestMsg = Json.newObject().put("path", "unknown").put("key", "testKey");
        mainActor.tell(requestMsg, probe.getRef());

        JsonNode expectedResponse = Json.toJson("Unknown request type");
        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
    }

//    @Test
//    public void testMainActorWithSearchRequest() throws Exception {
//        TestKit probe = new TestKit(system);
//        List<YouTubeVideo> response = new ArrayList<>();
//        response.add(new YouTubeVideo("new Id for sri", "Test Video", "Test Channel", "", "thumbnail1", null));
//        Mockito.when(mockYouTubeSearch.Search("bahubali", "home"))
//                .thenReturn(response);
//
//        JsonNode requestMsg = Json.newObject().put("path", "search").put("key", "bahubali");
//        mainActor.tell(requestMsg, probe.getRef());
//
//        JsonNode expectedResponse = Json.toJson("Unknown request type");
//        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
//    }

    /**
     * Test case for handling a tags request.
     * This test simulates a request for retrieving videos based on tags and checks that the MainActor
     * correctly responds with the list of YouTube videos that match the tag.
     * @author srinu kesari
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
     * @author srinu kesari
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
        // expectedResponse.put("youTubeVideosList",jsonResponseObject);
        System.out.println("expected response" + expectedResponse);
        System.out.println("jsonresponse" + jsonResponseObject);

        probe.expectMsgEquals(Duration.ofSeconds(5), expectedResponse);
    }
}
