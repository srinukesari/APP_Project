package actors;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pekko.stream.Materializer;

import controllers.*;

import play.libs.Json;
/**
 * The MainActor is the central actor that coordinates the handling of different types of requests 
 * by delegating the tasks to specific actors based on the request type.
 * <p>
 * It acts as a router, receiving incoming messages in the form of JsonNode objects, 
 * examining the request type, and forwarding the message to the appropriate child actor.
 * These child actors are responsible for specific operations related to search, profile, tags, 
 * and statistics. The MainActor also includes references to these child actors and manages their lifecycle.
 * @author team
 */

public class MainActor extends AbstractActor {
    private final ActorRef searchActor;
    private final ActorRef profileActor;
    private final ActorRef tagsActor;
    private final ActorRef statsActor;

    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    /**
     * Constructs the MainActor and initializes the child actors.
     * 
     * @param materializer The Akka Materializer used for stream processing.
     * @param youTubeSearch The YouTubeSearch instance used by the child actors.
     */

    public MainActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;

        this.searchActor = getContext().actorOf(Props.create(SearchActor.class, materializer, youTubeSearch), "search-actor");
        this.profileActor = getContext().actorOf(Props.create(ProfileActor.class, materializer,youTubeSearch), "profile-actor");
        this.tagsActor = getContext().actorOf(Props.create(TagsActor.class,materializer,youTubeSearch), "tags-actor");
        this.statsActor = getContext().actorOf(Props.create(MoreStatsActor.class,materializer,youTubeSearch), "morestats-actor");
    }

    /**
     * Factory method to create a Props instance for the MainActor.
     * 
     * @param materializer The Akka Materializer used for stream processing.
     * @param youTubeSearch The YouTubeSearch instance used by the child actors.
     * @return A Props object used to create the MainActor.
     */
    public static Props props(Materializer materializer,YouTubeSearch youTubeSearch) {
        return Props.create(MainActor.class, materializer, youTubeSearch);
    }
    /**
     * Defines the behavior of the MainActor. The actor listens for incoming messages of type JsonNode.
     * Based on the value of the "path" field in the JSON message, the actor forwards the message 
     * to the appropriate child actor for further processing.
     * <p>
     * Supported request types:
     * <ul>
     *     <li>"search" - forwards to the SearchActor</li>
     *     <li>"profile" - forwards to the ProfileActor</li>
     *     <li>"tags" - forwards to the TagsActor</li>
     *     <li>"hashTag" - forwards to the TagsActor</li>
     *     <li>"stats" - forwards to the MoreStatsActor</li>
     * </ul>
     * If the request type is unknown, it responds with a JSON object indicating the error.
     *
     * @return The Receive behavior of the MainActor.
     */

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, json -> {
                    System.out.println("check here srinu------>"+json);
                    String requestType = json.get("path").asText();
                    switch (requestType) {
                        case "search":
                            searchActor.forward(json, getContext());
                            break;
                        case "profile":
                            profileActor.forward(json, getContext());
                            break;
                        case "tags":
                            tagsActor.forward(json, getContext());
                            break;
                        case "hashTag":
                            tagsActor.forward(json, getContext());
                            break;
                        case "stats":
                            statsActor.forward(json, getContext());
                            break;
                        default:
                            sender().tell(Json.toJson("Unknown request type"), self());
                    }
                })
                .build();
    }
}
