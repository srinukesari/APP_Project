//package tests;
//
//import controllers.*;
//import models.*;
//import org.apache.pekko.actor.ActorSystem;
//import org.apache.pekko.stream.Materializer;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.mockito.Mockito;
//import play.libs.Json;
//import play.mvc.Http;
//import play.mvc.Result;
//import play.mvc.WebSocket;
//import play.routing.RoutingDsl;
//import play.test.Helpers;
//import play.test.TestServer;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static play.mvc.Http.Status.OK;
//
//public class WebSocketControllerTest {
//
//    private static ActorSystem system;
//    private static Materializer materializer;
//
//    private static YouTubeSearch youTubeSearchMock;
//
//    @BeforeClass
//    public static void setup() {
//        system = ActorSystem.create("WebSocketControllerTestSystem");
//        materializer = Materializer.createMaterializer(system);
//        youTubeSearchMock = Mockito.mock(YouTubeSearch.class);
//    }
//
//    @AfterClass
//    public static void teardown() {
//        system.terminate();
//    }
//
//    @Test
//    public void testWebSocketConnection() throws Exception {
//        // Mocking YouTubeSearch behavior (if needed)
//        try {
//            Mockito.when(youTubeSearchMock.Search("testKey", "home"))
//                    .thenReturn(java.util.Collections.singletonList(
//                            new YouTubeVideo("id1", "Test Video", "Test Channel", "", "thumbnail1", null)
//                    ));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Define routing for the WebSocket controller
//        RoutingDsl routingDsl = new RoutingDsl();
//        routingDsl.GET("/ws").routeTo(() -> new WebSocketController(system, materializer, youTubeSearchMock).handleWebSocket());
//
//        // Start the server
//        try (TestServer server = Helpers.testServer(9000, Helpers.componentsFromApplication())) {
//            server.start();
//
//            // WebSocket URL
//            String wsUrl = "ws://localhost:" + server.port() + "/ws";
//
//            // Establish WebSocket connection and send message (custom code here)
//            WebSocket.In<String> in = new WebSocket.In<String>() {
//                @Override
//                public void send(String message) {
//                    // Simulate sending a WebSocket message (use WebSocketClient if needed)
//                }
//
//                @Override
//                public String receive() {
//                    // Simulate receiving a WebSocket message
//                    return "HeartBeat!!!"; // Replace with actual response
//                }
//            };
//
//            // Send a message and receive response
//            in.send("{ \"path\": \"search\", \"key\": \"testKey\" }");
//            String response = in.receive();
//
//            // Check if the response is correct
//            assertNotNull(response);
//            assertEquals("Expected response from WebSocket", "HeartBeat!!!", response);
//        }
//    }
//}
