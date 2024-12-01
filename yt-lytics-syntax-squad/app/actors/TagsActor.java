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
 * The TagsActor is an Akka actor responsible for handling requests related to YouTube tags.
 * It interacts with the SearchController to fetch tags based on the provided type and identifier.
 * <p>
 * When the actor receives a message in the form of a JsonNode, it extracts the ID and type from 
 * the message and uses them to query the SearchController for the relevant tags. Once the tags 
 * are retrieved, the result is converted into JSON and sent back to the sender.
 * @author srinu.kesari
 */

public class TagsActor extends AbstractActor {
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    /**
     * Constructs the TagsActor with the given Materializer and YouTubeSearch instance.
     * 
     * @param materializer The Akka Materializer used for processing the response.
     * @param youTubeSearch The YouTubeSearch instance used to interact with YouTube.
     */
    public TagsActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;
    }

    /**
     * Defines the behavior of the TagsActor. The actor listens for messages of type JsonNode.
     * Upon receiving a message, it extracts the ID and type, then calls the SearchController 
     * to retrieve the tags associated with the given parameters. The result is converted to a 
     * JSON response and sent back to the sender.
     * <p>
     * The operation is performed asynchronously, and the final response is returned once the 
     * result is ready.
     *
     * @return The receive behavior of the actor, which processes incoming JsonNode messages.
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, json -> {
                    String id = json.get("key").asText();
                    String type = json.get("path").asText();
                    System.out.println("comes here info ----->"+type +" "+id);
                    SearchController searchController = new SearchController(youTubeSearch);
                    CompletableFuture<Result> results = searchController.tags(type,id);
                    Result result = results.join();

                    ConvertData convertData = new ConvertData();

                    CompletableFuture<JsonNode> jsonResponseFuture =
                            convertData.convertHttpEntityToJsonNode(result,materializer);

                    jsonResponseFuture.thenAccept(jsonResponse -> {
                        sender().tell(jsonResponse, self());
                    });
                })
                .build();
    }
}
