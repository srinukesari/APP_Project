import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import play.libs.Json;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import actors.*;
import controllers.*;
import models.*;
import org.apache.pekko.stream.Materializer;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MainActorTest {

    private static ActorSystem system;

    @Mock
    private YouTubeSearch youTubeSearch;  

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("MainActorTestSystem");
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSearchFunctionality() {
        // Create mock video data
        List<YouTubeVideo> mockVideos = new ArrayList<>();
        mockVideos.add(new YouTubeVideo("Id1", "title1", "TestChannel", "", "thumbnail1", null));
        mockVideos.add(new YouTubeVideo("Id2", "title2", "TestChannel2", "", "thumbnail2", null));

        try{
            Mockito.when(youTubeSearch.Search("testSearchKey", "home")).thenReturn(mockVideos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Materializer mockMaterializer = Mockito.mock(Materializer.class);
        ActorRef mainActor = system.actorOf(Props.create(MainActor.class, mockMaterializer, youTubeSearch));
        new TestKit(system) {{
            JsonNode inputJson = Json.newObject()
                    .put("path", "search")
                    .put("key", "testSearchKey");

            mainActor.tell(inputJson, getRef());
            JsonNode expectedResponse = Json.toJson(mockVideos);
            JsonNode actualResponse = expectMsgClass(JsonNode.class);
            assertEquals(expectedResponse, actualResponse);
        }};
    }
}
