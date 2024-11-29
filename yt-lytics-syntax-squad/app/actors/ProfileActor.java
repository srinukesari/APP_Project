package actors;

import org.apache.pekko.actor.AbstractActor;

import org.apache.pekko.stream.Materializer;
import org.apache.pekko.util.ByteString;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import controllers.SearchController;
import controllers.YouTubeSearch;
/**
 * The ProfileActor is an Akka actor responsible for handling requests related to YouTube channel profiles.
 * It interacts with the SearchController to fetch profile data for a specific YouTube channel.
 * <p>
 * When the actor receives a message in the form of a JsonNode, it extracts the channel name 
 * and uses it to request the channel's profile data, forwarding the results back to the sender.
 * @author sushmitha
 */

public class ProfileActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;
    /**
     * Constructs the ProfileActor with the given Materializer and YouTubeSearch instance.
     * 
     * @param materializer The Akka Materializer used for processing the response.
     * @param youTubeSearch The YouTubeSearch instance used for interacting with YouTube.
     */

    public ProfileActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;
    }
    /**
     * Defines the behavior of the ProfileActor. This actor listens for messages of type JsonNode.
     * Upon receiving a message, it extracts the channel name, queries the SearchController for
     * the profile information of the specified YouTube channel, and forwards the result back as a JSON response.
     * <p>
     * The response is processed asynchronously, and the final JSON result is sent back to the sender.
     *
     * @return The receive behavior of the actor, which processes incoming JsonNode messages.
     */

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, json -> {
                    // Extract the search key from the JSON message
                    String channelName = json.get("key").asText();

                    // Call the SearchController logic (or replicate it here)
                    SearchController searchController = new SearchController(youTubeSearch);
                    CompletableFuture<Result> results = searchController.profile(channelName);
                    Result result = results.join();

                    CompletableFuture<JsonNode> jsonResponseFuture =
                            ConvertData.convertHttpEntityToJsonNode(result,materializer);

                    // Send the JSON response back to the sender
                    jsonResponseFuture.thenAccept(jsonResponse -> {
                        System.out.println("check the response------> "+ jsonResponse);
                        sender().tell(jsonResponse, self());
                    });
                })
                .build();
    }
}
