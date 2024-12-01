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
 * The MoreStatsActor is an Akka actor responsible for handling requests related to YouTube search statistics.
 * It interacts with the SearchController to perform search operations and retrieve statistics.
 * <p>
 * When the actor receives a message in the form of a JsonNode, it extracts the search key and uses it
 * to perform a search and fetch relevant statistics, forwarding the results back to the sender.
 * @author sahiti
 */
public class MoreStatsActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

     /**
     * Constructs the MoreStatsActor with the given Materializer and YouTubeSearch instance.
     * 
     * @param materializer The Akka Materializer used for processing the response.
     * @param youTubeSearch The YouTubeSearch instance used for searching YouTube.
     */
    public MoreStatsActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;
    }

    /**
     * Defines the behavior of the MoreStatsActor. This actor listens for messages of type JsonNode.
     * Upon receiving a message, it extracts the search key, performs a search using the 
     * SearchController, and retrieves statistics related to the search term.
     * <p>
     * If the search or statistics retrieval fails (e.g., no results are found), an error response
     * is sent back. Otherwise, the actor returns the processed results in JSON format.
     *
     * @return The receive behavior of the actor, which processes incoming JsonNode messages.
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, json -> {
                    // Extract the search key from the JSON message
                    System.out.println("Received message: " + json.toString());
                    String searchKey = json.get("key").asText();


                    // Call the SearchController logic (or replicate it here)
                    SearchController searchController = new SearchController(youTubeSearch);
                    CompletableFuture<Result> searchResultFuture = searchController.search(searchKey);
                    Result searchResult333 = searchResultFuture.join();

                    ConvertData convertData = new ConvertData();

                    CompletableFuture<JsonNode> jsonResponseFuture333 =
                            convertData.convertHttpEntityToJsonNode(searchResult333,materializer);
                    
                    jsonResponseFuture333.thenAccept(result -> {
                        CompletableFuture<Result> results = searchController.displayStats(searchKey);
                        Result result1 = results.join();

                        if (result1.status() == 404 || result1.status() == 204) {
                            JsonNode errorResponse = Json.newObject()
                                    .put("error", "No search results found for the given terms for result status 404 204.");
                            sender().tell(errorResponse, self());
                        }else{
                        CompletableFuture<JsonNode> jsonResponseFuture =
                                convertData.convertHttpEntityToJsonNode(result1,materializer);
                        jsonResponseFuture.thenAccept(jsonResponse -> {
                            System.out.println("check the response in the else condition ------> "+ jsonResponse);
                            sender().tell(jsonResponse, self());
                        });
                        }
                });
                })
                .build();
    }
}
