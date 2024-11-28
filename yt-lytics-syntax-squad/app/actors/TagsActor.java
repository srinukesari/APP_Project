package actors;

import akka.actor.AbstractActor;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.util.ByteString;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import controllers.SearchController;
import controllers.YouTubeSearch;

public class TagsActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    public TagsActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, json -> {
                    String id = json.get("key").asText();
                    String type = json.get("path").asText();

                    SearchController searchController = new SearchController(youTubeSearch);
                    CompletableFuture<Result> results = searchController.tags(type,id);
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
