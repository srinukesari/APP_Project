package controllers;
import actors.MainActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.apache.pekko.stream.javadsl.Flow;
import org.apache.pekko.stream.Materializer;

import akka.util.Timeout;

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

public class WebSocketController extends Controller {

    private final ActorSystem actorSystem;
    private final Materializer materializer;
    private final YouTubeSearch youTubeSearch;

    @Inject
    public WebSocketController(ActorSystem actorSystem,
                               Materializer materializer,
                               YouTubeSearch youtTubeSearch) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        this.youTubeSearch = youtTubeSearch;
    }

    public WebSocket handleWebSocket() {
        String uniqueMainActor = "main-actor" + UUID.randomUUID().toString();
        ActorRef mainActor = actorSystem.actorOf(MainActor.props(materializer,youTubeSearch), uniqueMainActor);
        return WebSocket.Text.accept(request -> {
            return createWebSocketFlow(mainActor);
        });
    }

    private Flow<String, String, ?> createWebSocketFlow(ActorRef actorRef) {
        return Flow.<String>create()
                .keepAlive(Duration.ofSeconds(10), () -> "ping")
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

    public CompletionStage<JsonNode> handleMessage(JsonNode jsonNode, ActorRef mainActor) {
        // Timeout timeout = Timeout.create(Duration.ofSeconds(20));
        Timeout timeout = Timeout.create(Duration.ofSeconds(60));


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
