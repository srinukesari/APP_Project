package actors;

import org.apache.pekko.actor.AbstractActor;

import org.apache.pekko.stream.Materializer;
import org.apache.pekko.util.ByteString;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;
import org.apache.pekko.actor.Cancellable;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.time.Duration;


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
 * @author aniket,srinu.kesari
 */
public class SearchActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;
    private Cancellable periodicTask;
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

//    /**
//     * Factory method to create a Props instance for the SearchActor.
//     *
//     * @param materializer The Akka Materializer used for stream processing.
//     * @param youTubeSearch The YouTubeSearch instance used by the child actors.
//     * @return A Props object used to create the SearchActor.
//     */
//    public static Props props(Materializer materializer,YouTubeSearch youTubeSearch) {
//        return Props.create(SearchActor.class, materializer, youTubeSearch);
//    }

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
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, json -> {
                    if(json.has("key")){
                        String searchKey = json.get("key").asText();
                        performSearch(searchKey);
                    }
                })
                .build();
    }

    /**
     * Performs a search operation based on the given search key and sends back the results in the form of a JSON response.
     *
     * This method initiates a search using the provided search key, waits for the results, converts the results to a JSON
     * format, and then sends the JSON response back to the sender actor.
     *
     * @param searchKey The key used for searching, which can be a query for the YouTube search.
     */
    private void performSearch(String searchKey) {
        SearchController searchController = new SearchController(youTubeSearch);
        CompletableFuture<Result> results = searchController.search(searchKey);
        Result result = results.join();

        ConvertData convertData = new ConvertData();

        CompletableFuture<JsonNode> jsonResponseFuture =
                convertData.convertHttpEntityToJsonNode(result,materializer);

        // Send the JSON response back to the sender
        jsonResponseFuture.thenAccept(jsonResponse -> {
            sender().tell(jsonResponse, self());
        });
    }
}
