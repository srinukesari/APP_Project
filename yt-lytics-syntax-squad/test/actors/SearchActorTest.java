import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

// Import the actors and dependencies for testing
import actors.*;
import controllers.*;
import models.*;
import org.apache.pekko.stream.Materializer; 

public class SearchActorTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("TestSystem");
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testSearchActorProcessingSearchRequest() throws IOException {
        final TestKit probe = new TestKit(system);

        // Create mock data for YouTube search results
        List<YouTubeVideo> mockVideos = new ArrayList<>();
        mockVideos.add(new YouTubeVideo("Id1", "title1", "TestChannel", "    ", "thumbnail1", null));
        mockVideos.add(new YouTubeVideo("Id2", "title2", "TestChannel2", "    ", "thumbnail2", null));

        // Mock the YouTubeSearch class
        YouTubeSearch mockYouTubeSearch = mock(YouTubeSearch.class);
        
        // Mock the Search method to return the mock data when called
        when(mockYouTubeSearch.Search(eq("Akka"), anyString())).thenReturn(mockVideos);

        // Mock the Materializer
        Materializer mockMaterializer = mock(Materializer.class);

        // Create the SearchActor, passing the Materializer and the mocked YouTubeSearch object
        ActorRef searchActor = system.actorOf(SearchActor.props(mockMaterializer, mockYouTubeSearch));

        // Create a JSON request with a search query for "Akka"
        JsonNode requestJson = Json.newObject().put("key", "Akka");

        // Send the search request to the SearchActor
        searchActor.tell(requestJson, probe.getRef());

        // Expect a JSON response containing the search results
        JsonNode response = probe.expectMsgClass(JsonNode.class);

        // Verify that the response contains the mocked video data
        assertNotNull(response);
        assertEquals("title1", response.get(0).get("title").asText());
        assertEquals("TestChannel", response.get(0).get("description").asText());

        assertEquals("title2", response.get(1).get("title").asText());
        assertEquals("TestChannel2", response.get(1).get("description").asText());
    }
}
