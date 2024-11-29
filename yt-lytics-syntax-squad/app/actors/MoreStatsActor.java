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

public class MoreStatsActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    public MoreStatsActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;
    }


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
                    
                    CompletableFuture<JsonNode> jsonResponseFuture333 =
                            ConvertData.convertHttpEntityToJsonNode(searchResult333,materializer);
                    
                    jsonResponseFuture333.thenAccept(result -> {
                    // if (result.status() == 404 || result.status() == 204) {
                    //     // Send back an error message if no results found
                    //     JsonNode errorResponse = Json.newObject()
                    //         .put("error", "No search results found for the given terms.");
                    //     sender().tell(errorResponse, self());
                    // } else {
                        // Proceed to get word stats from displayStats
                        // CompletableFuture<Result> statsResultFuture = searchController.displayStats(searchKey);
                        // statsResultFuture.thenAccept(statsResult -> {
                        //     if (statsResult.status() == 200) {
                        //         CompletableFuture<JsonNode> jsonResponseFuture = 
                        //             ConvertData.convertHttpEntityToJsonNode(statsResult, getContext().system().materializer());
                        //         jsonResponseFuture.thenAccept(jsonResponse -> {
                        //             sender().tell(jsonResponse, self());
                        //         });
                        //     } else {
                        //         JsonNode errorResponse = Json.newObject()
                        //             .put("error", "Failed to get word stats.");
                        //         sender().tell(errorResponse, self());
                        //     }
                        // });
                        CompletableFuture<Result> results = searchController.displayStats(searchKey);
                        Result result1 = results.join();

                        if (result1.status() == 404 || result1.status() == 204) {
                        // Send back an error message instead of a plain string
                            JsonNode errorResponse = Json.newObject()
                                    .put("error", "No search results found for the given terms for result status 404 204.");
                            sender().tell(errorResponse, self());
                        }else{
                        CompletableFuture<JsonNode> jsonResponseFuture =
                                ConvertData.convertHttpEntityToJsonNode(result1,materializer);

                        // Send the JSON response back to the sender
                        jsonResponseFuture.thenAccept(jsonResponse -> {
                            System.out.println("check the response in the else condition ------> "+ jsonResponse);
                            // System.out.println("Word Stats Response then: " + jsonResponse.toString());
                            sender().tell(jsonResponse, self());
                        });
                        }
                    // }
                });

                    
                })
                .build();
    }
}
