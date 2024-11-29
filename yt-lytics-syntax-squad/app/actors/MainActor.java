package actors;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pekko.stream.Materializer;

import controllers.*;

import play.libs.Json;

public class MainActor extends AbstractActor {
    private final ActorRef searchActor;
    private final ActorRef profileActor;
    private final ActorRef tagsActor;
    private final ActorRef statsActor;

    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    public MainActor(Materializer materializer, YouTubeSearch youTubeSearch) {
        this.materializer = materializer;
        this.youTubeSearch = youTubeSearch;

        this.searchActor = getContext().actorOf(Props.create(SearchActor.class, materializer, youTubeSearch), "search-actor");
        this.profileActor = getContext().actorOf(Props.create(ProfileActor.class, materializer,youTubeSearch), "profile-actor");
        this.tagsActor = getContext().actorOf(Props.create(TagsActor.class,materializer,youTubeSearch), "tags-actor");
        this.statsActor = getContext().actorOf(Props.create(MoreStatsActor.class,materializer,youTubeSearch), "morestats-actor");
    }

    // Method to create Props for the MainActor
    public static Props props(Materializer materializer,YouTubeSearch youTubeSearch) {
        return Props.create(MainActor.class, materializer, youTubeSearch);
    }

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
