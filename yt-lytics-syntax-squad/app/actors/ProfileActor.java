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

public class ProfileActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    public ProfileActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;
    }


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
