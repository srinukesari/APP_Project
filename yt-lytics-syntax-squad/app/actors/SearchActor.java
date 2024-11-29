package actors;

import org.apache.pekko.actor.AbstractActor;

import org.apache.pekko.stream.Materializer;
import org.apache.pekko.util.ByteString;
import org.apache.pekko.actor.Props;


import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import actors.*;
import controllers.SearchController;
import controllers.YouTubeSearch;


/**
 * The SearchActor is an Akka actor responsible for handling search requests related to YouTube.
 * It interacts with the SearchController to perform searches using a provided search key 
 * and returns the results in JSON format.
 * <p>
 * When the actor receives a JSON message containing a search key, it performs a search by calling 
 * the SearchController. Once the results are obtained, it converts them into a JSON response 
 * and sends it back to the sender.
 * @author aniket
 */
public class SearchActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    /**
     * Constructs the SearchActor with the given Materializer and YouTubeSearch instance.
     * 
     * @param materializer The Akka Materializer used for processing the response.
     * @param youTubeSearch The YouTubeSearch instance used to interact with YouTube.
     */
    public SearchActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;
    }
    /**
     * Defines the behavior of the SearchActor. The actor listens for messages of type JsonNode.
     * Upon receiving a message, it extracts the search key from the JSON, uses the SearchController 
     * to perform the search, and converts the result to JSON format. Finally, the result is sent back to the sender.
     * <p>
     * The search operation is performed asynchronously, and once the results are ready, 
     * they are converted to JSON and forwarded to the sender.
     *
     * @return The receive behavior of the actor, which processes incoming JsonNode messages.
     */

    // Method to create Props for the MainActor
    public static Props props(Materializer materializer,YouTubeSearch youTubeSearch) {
        return Props.create(SearchActor.class, materializer, youTubeSearch);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, json -> {
                    // Extract the search key from the JSON message
                    String searchKey = json.get("key").asText();

                    SearchController searchController = new SearchController(youTubeSearch);
                    CompletableFuture<Result> results = searchController.search(searchKey);
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
