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


public class SearchActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    public SearchActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;
    }

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
