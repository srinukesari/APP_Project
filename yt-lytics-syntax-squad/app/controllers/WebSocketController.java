package controllers;
import actors.MainActor;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.pattern.Patterns;
import org.apache.pekko.stream.javadsl.Sink;
import org.apache.pekko.stream.javadsl.Source;
import org.apache.pekko.stream.javadsl.Flow;
import org.apache.pekko.stream.Materializer;

import org.apache.pekko.util.Timeout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;
import play.mvc.Result;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

import play.libs.F;

import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import java.time.Duration;

import controllers.*;
/**
 * WebSocketController handles WebSocket communication, managing connections, and
 * relaying messages between the client and the main actor.
 * <p>
 * The controller provides an endpoint for establishing WebSocket connections, where messages
 * are passed to a main actor, which processes them and returns responses. The WebSocket 
 * connection is kept alive, and heartbeats are used to check the connection's health.
 * @author Team
 */

public class WebSocketController extends Controller {

    private final ActorSystem actorSystem;
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;
    
    /**
     * Constructs the WebSocketController with the given dependencies.
     *
     * @param actorSystem The ActorSystem used to create and manage actors.
     * @param materializer The Materializer used to handle Akka Streams.
     * @param youTubeSearch The YouTubeSearch service used for searching YouTube.
     */

    @Inject
    public WebSocketController(ActorSystem actorSystem,
                               Materializer materializer,
                               YouTubeSearch youtTubeSearch) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        this.youTubeSearch = youtTubeSearch;
    }

    /**
     * Creates and handles a WebSocket connection, setting up the main actor for handling
     * the WebSocket messages.
     *
     * @return A WebSocket instance that accepts text messages and processes them.
     */

    public WebSocket handleWebSocket() {
        String uniqueMainActor = "main-actor" + UUID.randomUUID().toString();
        ActorRef mainActor = actorSystem.actorOf(MainActor.props(materializer,youTubeSearch), uniqueMainActor);
        return WebSocket.Text.accept(request -> {
            return createWebSocketFlow(mainActor);
        });
    }
    /**
     * Creates a WebSocket flow that processes incoming messages and sends responses.
     * The flow listens for "ping" messages to check the connection status and forwards
     * other messages to the main actor for processing.
     * 
     * @param actorRef The actor responsible for processing incoming messages.
     * @return A Flow that handles incoming WebSocket messages.
     */

    private Flow<String, String, ?> createWebSocketFlow(ActorRef actorRef) {
        return Flow.<String>create()
                .keepAlive(Duration.ofSeconds(30), () -> "ping")
                .mapAsync(1, message -> {
                        System.out.println("incomming msg -----> "+message);
                        if ("ping".equals(message)) {
                            System.out.println("Received heartbeat");
                            return CompletableFuture.completedFuture("HeartBeat!!!");
                        }
                        JsonNode jsonNode = Json.parse(message); // Parse incoming message
                        return handleMessage(jsonNode,actorRef)
                                .thenApply(response -> {
                                    System.out.println("inside handle ----"+response.toString());
                                    return response.toString();
                                });
                    });
    }
    /**
     * Handles the processing of a message received over the WebSocket.
     * The message is forwarded to the main actor for processing, and the response is returned
     * as a JSON object. If any error occurs, an error JSON response is returned.
     *
     * @param jsonNode The incoming message as a JSON node.
     * @param mainActor The actor that processes the message.
     * @return A CompletionStage of the JSON response from the actor.
     */

    public CompletionStage<JsonNode> handleMessage(JsonNode jsonNode, ActorRef mainActor) {
        Timeout timeout = Timeout.create(Duration.ofSeconds(20));

        Future<Object> scalaFuture = Patterns.ask(mainActor, jsonNode, timeout);

        CompletionStage<Object> javaFuture = FutureConverters.toJava(scalaFuture);

        return javaFuture.thenApply(response -> {
            if (response instanceof JsonNode) {
                System.out.println("check ------> "+ response);
                return (JsonNode) response; // Respond with JSON
            } else {
                throw new RuntimeException("Unexpected response type");
//                return badRequest("Unexpected response type");
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            // Return an error JSON object
            ObjectNode errorNode = Json.newObject();
            errorNode.put("error", "Error processing request");
            errorNode.put("details", ex.getMessage());
            return errorNode;
//            return internalServerError("Error processing request");
        });
    }
}
